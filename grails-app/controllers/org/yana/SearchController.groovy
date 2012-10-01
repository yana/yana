package org.yana

import org.compass.core.engine.SearchEngineQueryParseException
import grails.plugins.springsecurity.Secured
import grails.converters.JSON
import org.yana.Node
import org.yana.springacl.DefaultProjectAccess
import org.yana.springacl.ProjectAccess
import org.yana.Project

@DefaultProjectAccess(ProjectAccess.Level.read)
class SearchController {

    def iconService
    def springSecurityService
    def searchableService
    def jsonService
    def xmlService
    def projectService

    /**
     * Search Nodes only
     */
    def index = {
        if(!params.project){
            response.status=406
            render(text:"Project is required")
            return
        }
        String path = iconService.getSmallIconPath()
        if (!params.q?.trim()) {
            return [:]
        }
        params.sort='nameSort'
        if(!(params.order in ['asc','desc'])){
            params.order='asc'
        }
        def Project proj=projectService.findProject(params.project)
        try {
            def results = Node.search( {
                                           must(term("project", params.project.toLowerCase()))
                                           must(queryString(params.q))
                                       }, params.subMap(['offset', 'max', 'sort', 'order']))
            def total = Node.countHits( {
                                           must(term("project", params.project.toLowerCase()))
                                           must(queryString(params.q))
                                       })
            if (params.format) {


                ArrayList nodes = results.results

                switch (params.format.toLowerCase()) {
                    case 'xml':
                        def xml = xmlService.formatNodes(nodes.collect{Node.get(it.id)})
                        render(text: xml, contentType: "text/xml")
                        break;
                    case 'json':
                        def json = jsonService.formatNodes(nodes.collect {Node.get(it.id)})
                        render(text: json, contentType: "text/json")
                        break;
                }
            } else {
                return [searchResult: results, total: total, path: path]
            }
        } catch (SearchEngineQueryParseException ex) {
            return [parseException: true]
        }
    }

    /**
     * Perform a bulk index of every searchable object in the database
     */
    def indexAll = {
        Thread.start {
            searchableService.index()
        }
        render("bulk index started in a background thread")
    }

    /**
     * Perform a bulk index of every searchable object in the database
     */
    def unindexAll = {
        searchableService.unindex()
        render("unindexAll done")
    }
}
