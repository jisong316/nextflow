/*
 * Copyright 2018, University of Tübingen, Quantitative Biology Center (QBiC)
 * Copyright 2013-2019, Centre for Genomic Regulation (CRG)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextflow.trace

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import groovyx.gpars.agent.Agent
import nextflow.Session
import nextflow.processor.TaskHandler
import nextflow.util.SimpleHttpClient

/**
 * Send out messages via HTTP to a configured URL on different workflow
 * execution events.
 *
 * @author Sven Fillinger <sven.fillinger@qbic.uni-tuebingen.de>
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@Slf4j
@CompileStatic
class WebLogObserver implements TraceObserver{

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC")

    /**
     * Workflow identifier, will be taken from the Session() object later
     */
    private String runName

    /**
     * Store the sessions unique ID for downstream reference purposes
     */
    private String runId

    /**
     * Not a HTTP header, but a message header with some general workflow info
     */
    private static JsonSlurper SLURPER = new JsonSlurper()

    /**
     * The default url is localhost
     */
    static public String DEF_URL = 'http://localhost'

    /**
     * Simple http client object that will send out messages
     */
    private SimpleHttpClient httpClient = new SimpleHttpClient()

    /**
     * An agent for the http request in an own thread
     */
    private Agent<WebLogObserver> webLogAgent

    /**
     * Constructor that consumes a URL and creates
     * a basic HTTP client.
     * @param url The target address for sending messages to
     */
    WebLogObserver(String url) {
        this.httpClient.setUrl(checkUrl(url))
        this.webLogAgent = new Agent<>(this)
    }

    /**
     * only for testing purpose -- do not use
     */
    protected WebLogObserver() {

    }

    /**
     * Check the URL and create an HttpPost() object. If a invalid i.e. protocol is used,
     * the constructor will raise an exception.
     *
     * The RegEx was taken and adapted from http://urlregex.com
     *
     * @param url String with target URL
     * @return The requested url or the default url, if invalid
     */
    protected String checkUrl(String url){
        if( url =~ "^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]" ) {
            return url
        }
        throw new IllegalArgumentException("Only http or https are supported protocols -- The given URL was: ${url}")
    }

    /**
     * On workflow start, submit a message with some basic
     * information, like Id, activity and an ISO 8601 formatted
     * timestamp.
     * @param session The current Nextflow session object
     */
    @Override
    void onFlowStart(Session session) {
        // This is either set by the user or via Nextflow name generator
        runName = session.getRunName()
        runId = session.getUniqueId()
        asyncHttpMessage("started")
    }

    /**
     * Send an HTTP message when the workflow is completed.
     */
    @Override
    void onFlowComplete() {
        asyncHttpMessage("completed")
    }

    /**
     * Send an HTTP message when a process has been submitted
     *
     * @param handler A {@link TaskHandler} object representing the task submitted
     * @param trace A {@link TraceRecord} object holding the task metadata and runtime info
     */
    @Override
    void onProcessSubmit(TaskHandler handler, TraceRecord trace) {
        asyncHttpMessage("process_submitted", trace)
    }

    /**
     * Send an HTTP message, when a process has started
     *
     * @param handler A {@link TaskHandler} object representing the task started
     * @param trace A {@link TraceRecord} object holding the task metadata and runtime info
     */
    @Override
    void onProcessStart(TaskHandler handler, TraceRecord trace) {
        asyncHttpMessage("process_started", trace)
    }

    /**
     * Send an HTTP message, when a process completed
     *
     * @param handler A {@link TaskHandler} object representing the task completed
     * @param trace A {@link TraceRecord} object holding the task metadata and runtime info
     */
    @Override
    void onProcessComplete(TaskHandler handler, TraceRecord trace) {
        asyncHttpMessage("process_completed", trace)
    }

    /**
     * Send an HTTP message, when a workflow has failed
     *
     * @param handler A {@link TaskHandler} object representing the task that caused the workflow execution to fail (it may be null)
     * @param trace A {@link TraceRecord} object holding the task metadata and runtime info (it may be null)
     */
    @Override
    void onFlowError(TaskHandler handler, TraceRecord trace) {
        asyncHttpMessage("error", trace)
    }

    /**
     * Little helper method that sends a HTTP POST message as JSON with
     * the current run status, ISO 8601 UTC timestamp, run name and the TraceRecord
     * object, if present.
     * @param event The current run status. One of {'started', 'process_submit', 'process_start',
     * 'process_complete', 'error', 'completed'}
     * @param trace A TraceRecord object that contains current process information
     */
    protected void sendHttpMessage(String event, TraceRecord trace = null){

        // Set the message info
        final time = new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", UTC)

        final message = new HashMap(5)
        message.runName = runName
        message.runId = runId
        message.event = event
        message.runStatus = event // deprecated to be removed
        message.utcTime = time

        // Append the trace object if present
        if (trace)
            message.trace = trace.store

        // The actual HTTP request
        httpClient.sendHttpMessage(JsonOutput.toJson(message))
        logHttpResponse()
    }

    /**
     * Asynchronous HTTP POST request wrapper.
     * @param event The workflow run status
     * @param trace A TraceRecord object with workflow information
     * @return A Java string, that contains the HTTP request response
     */
    protected void asyncHttpMessage(String event, TraceRecord trace = null){
        webLogAgent.send{sendHttpMessage(event, trace)}
    }

    /**
     * Little helper function that can be called for logging upon an incoming HTTP response
     */
    protected void logHttpResponse(){
        def statusCode = httpClient.getResponseCode()
        if (statusCode == 200)
            log.debug "Successfully send message to ${httpClient.getUrl()} -- received status code 200"
        else {
            def msg = """\
                Failed to send message to ${httpClient.getUrl()} -- received 
                - status code : $statusCode    
                - response msg: ${httpClient.getResponse()}  
                """
                .stripIndent()
            log.debug msg
        }
    }

}
