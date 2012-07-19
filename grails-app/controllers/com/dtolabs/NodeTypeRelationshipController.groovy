package com.dtolabs

import com.dtolabs.NodeType
import com.dtolabs.NodeTypeRelationship
import org.springframework.dao.DataIntegrityViolationException
import com.dtolabs.NodeTypeRelationship
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_YANA_ADMIN','ROLE_YANA_ARCHITECT','ROLE_YANA_SUPERUSER'])
class NodeTypeRelationshipController {

	def iconService
	def xmlService
	def jsonService
	def webhookService
	
	
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
		if(params.format && params.format!='none'){
			def ntrs = NodeTypeRelationship.list(params)
			switch(params.format.toLowerCase()){
				case 'xml':
					def xml = xmlService.formatNodeTypeRelationships(ntrs)
					render(text: xml, contentType: "text/xml")
					break;
				case 'json':
					def json = jsonService.formatNodeTypeRelationships(ntrs)
					render(text:json, contentType: "text/json")
					break;
			}
		}else{
        	params.max = Math.min(params.max ? params.int('max') : 10, 100)
			[nodeTypeRelationshipInstanceList: NodeTypeRelationship.list(params), nodeTypeRelationshipInstanceTotal: NodeTypeRelationship.count()]
		}
    }
	
	def listapi(){
		switch(request.method){
			case "POST":
				def json = request.JSON
				this.list()
				break
		  }
		return
	}
	 
	def api(){
		switch(request.method){
			case "POST":
				def json = request.JSON
				def nodeTypeRelationship = new NodeTypeRelationship(params)
				if(nodeTypeRelationship){
					if (!nodeTypeRelationship.save(flush: true)) {
						response.status = 400 //Bad Request
						render "NodeTypeRelationship Creation Failed"
					}else{
						ArrayList nodeTypeRelationships = [nodeTypeRelationship]
						webhookService.postToURL('nodetyperelationship', nodeTypeRelationships,'create')
						
						response.status = 200
						if(params.format && params.format!='none'){
							switch(params.format.toLowerCase()){
								case 'xml':
									def xml = xmlService.formatNodeTypeRelationships(nodeTypeRelationships)
									render(text: xml, contentType: "text/xml")
									break;
								case 'json':
									def jsn = jsonService.formatNodeTypeRelationships(nodeTypeRelationships)
									render(text: jsn, contentType: "text/json")
									break;
							}
						}else{
							render "Successfully Created."
						}
						
					}
				}else{
					  response.status = 404 //Not Found
					  render "${params.id} not found."
				}
				break
			case "GET":
				def json = request.JSON
				this.show()
				break
			case "PUT":
				def json = request.JSON
				this.update()
				break
			case "DELETE":
				def json = request.JSON
				if(params.id){
					def nodetyperelationship = NodeTypeRelationship.get(params.id)
					if(nodetyperelationship){
						try{
							nodetyperelationship.delete(flush:true)
							
							ArrayList nodetypes = [nodetyperelationship]
							webhookService.postToURL('nodetyperelationship', nodetypes,'delete')
							
							response.status = 200
							render "Successfully Deleted."
						}catch(org.springframework.dao.DataIntegrityViolationException e) {
							NodeTypeRelationship.withSession { session ->
								session.clear()
							}
	
							response.status = 403 //Bad Request
							render "Referential Integrity Violation."
						}
					}else{
					  response.status = 404 //Not Found
					  render "${params.id} not found."
					}
				}else{
					response.status = 400 //Bad Request
					render """DELETE request must include the id"""
				}
				break
		  }
		return
	}
	
	

    def create() {
		def cardinality = ['0':'0','1':'1','2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','10':'10','999999999':'*']
        [nodeTypeRelationshipInstance: new NodeTypeRelationship(params),cardinality:cardinality]
    }

    def save() {
		def cardinality = ['0':'0','1':'1','2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','10':'10','999999999':'*']
		def parent = NodeType.get(params.parent.id.toLong())
		def child = NodeType.get(params.child.id.toLong())
		def exists= NodeTypeRelationship.findByParentAndChild(parent,child)
		
		if(!exists){
	        def nodeTypeRelationshipInstance = new NodeTypeRelationship(params)
	        if (!nodeTypeRelationshipInstance.save(flush: true)) {
	            render(view: "create", model: [nodeTypeRelationshipInstance: nodeTypeRelationshipInstance,cardinality:cardinality])
	            return
	        }
	
			flash.message = message(code: 'default.created.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), nodeTypeRelationshipInstance.id])
	        redirect(action: "show", id: nodeTypeRelationshipInstance.id)
		}else{
			flash.message = message("Existing relationship for that Parent and child NodeType already exists. Please try again.")
	        render(view: "create", model: [nodeTypeRelationshipInstance: nodeTypeRelationshipInstance,cardinality:cardinality])
			return
		}
    }

    def show() {
		String path = iconService.getMedIconPath()
        def nodeTypeRelationshipInstance = NodeTypeRelationship.get(params.id)
        if (!nodeTypeRelationshipInstance) {
			if(params.format){
				response.status = 404 //Not Found
				render "${params.id} not found."
			} else {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), params.id])
				redirect(action: "list")
				return
			}
        } else {
			if(params.format && params.format!='none') {
				ArrayList nodetyperelationships = [nodeTypeRelationshipInstance]
				switch(params.format.toLowerCase()){
					case 'xml':
						def xml = xmlService.formatNodeTypeRelationships(nodetyperelationships)
						render(text: xml, contentType: "text/xml")
						break;
					case 'json':
						def json = jsonService.formatNodeTypeRelationships(nodetyperelationships)
						render(text:json, contentType: "text/json")
						break;
				}
			} else {
				[nodeTypeRelationshipInstance: nodeTypeRelationshipInstance,path:path]
			}
        }        
    }

    def edit() {
        def nodeTypeRelationshipInstance = NodeTypeRelationship.get(params.id)
        if (!nodeTypeRelationshipInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), params.id])
            redirect(action: "list")
            return
        }
		def cardinality = ['0':'0','1':'1','2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','10':'10','999999999':'*']
        [nodeTypeRelationshipInstance: nodeTypeRelationshipInstance,cardinality:cardinality]
    }

    def update() {
		def cardinality = ['0':'0','1':'1','2':'2','3':'3','4':'4','5':'5','6':'6','7':'7','8':'8','9':'9','10':'10','999999999':'*']
        def nodeTypeRelationshipInstance = NodeTypeRelationship.get(params.id)
		if(!params.roleName){
			flash.message = "Rolename is a required field"
			render(view: "edit", model: [nodeTypeRelationshipInstance: nodeTypeRelationshipInstance,cardinality:cardinality])
		}else{
	        if (!nodeTypeRelationshipInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), params.id])
	            redirect(action: "list")
	            return
	        }
	
	        if (params.version) {
	            def version = params.version.toLong()
	            if (nodeTypeRelationshipInstance.version > version) {
	                nodeTypeRelationshipInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
	                          [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship')] as Object[],
	                          "Another user has updated this NodeTypeRelationship while you were editing")
	                render(view: "edit", model: [nodeTypeRelationshipInstance: nodeTypeRelationshipInstance,cardinality:cardinality])
	                return
	            }
	        }
	
	        nodeTypeRelationshipInstance.properties = params
	
	        if (!nodeTypeRelationshipInstance.save(flush: true)) {
	            render(view: "edit", model: [nodeTypeRelationshipInstance: nodeTypeRelationshipInstance])
	            return
	        }
	
			flash.message = message(code: 'default.updated.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), nodeTypeRelationshipInstance.id])
	        redirect(action: "show", id: nodeTypeRelationshipInstance.id)
		}
    }

    def delete() {
        def nodeTypeRelationshipInstance = NodeTypeRelationship.get(params.id)
        if (!nodeTypeRelationshipInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), params.id])
            redirect(action: "list")
            return
        }

        try {
            nodeTypeRelationshipInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'nodeTypeRelationship.label', default: 'NodeTypeRelationship'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
