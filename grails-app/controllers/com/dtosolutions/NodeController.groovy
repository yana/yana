package com.dtosolutions

import grails.converters.JSON
import java.util.Date;
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_YANA_ADMIN','ROLE_YANA_USER','ROLE_YANA_ARCHITECT','ROLE_YANA_SUPERUSER'])
class NodeController {
	
	def iconService
	def springSecurityService
	def xmlService
	def webhookService
	
    static allowedMethods = [get: "POST", save: "POST", update: "POST", delete: "POST"]
   
	/*
	 * Restful function to handle routing
	 * URLMapping wants to route everything to node or take over routing for node; needed to build
	 * routing function to handle REST handling to do custom routing for anything that doesn't 
	 * look like it is handled by controller
	 */
	def api(){
		switch(request.method){
			case "POST":
				def json = request.JSON
				this.save()
				break
			case "GET":
				this.show()
				break
			case "PUT":
				this.update()
				break
			case "DELETE":
				this.delete()
				break
		  }
	}
	
	def webhook(){
		switch(request.method){
			case "POST":
			   	def json = request.JSON
				params.service = params.controller
				def webhookInstance = Webhook.findByUrlAndService(params.url,params.controller)
				if(!webhookInstance){
					webhookInstance = new Webhook(params)
				}
			    if (!webhookInstance.save(flush: true)) {
			        println("INVALID/MALFORMED DATA: PLEASE SEE DOCS FOR 'JSON' FORMED STRING AND PLEASE TRY AGAIN.")
			        return
			    }
				flash.message = message(code: 'default.created.message', args: [message(code: 'webhook.label', default: 'Webhook'), webhookInstance.id])
		        redirect(action: "show", id: webhookInstance.id)
				break
			default:
				println("INCORRECT REQUEST METHOD: EXPECTING POST METHOD. PLEASE TRY AGAIN.")
				break;
	   }
   }
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
		def nodes = Node.list(params)
		if(params.format){
			switch(params.format){
				case 'xml':
				case 'XML':
					def xml = xmlService.formatNodes(nodes)
					render(text: xml, contentType: "text/xml")
					break;
			}
		}else{
        	params.max = Math.min(params.max ? params.int('max') : 10, 100)
			[nodeInstanceList: Node.list(params), nodeInstanceTotal: Node.count()]
		}
    }

    def create() {
        [nodeList: Node.list(),nodeInstance: new Node(params)]
    }

    def save() {
		def parents
		if(params.parents){
			Long[] adults = Eval.me("${params.parents}")
			if(params.parents){ parents = Node.findAll("from Node as N where N.id IN (:ids)",[ids:adults]) }
		}
		
		def children
		if(params.children){
			Long[] kinder = Eval.me("${params.children}")
			if(params.children){ children = Node.findAll("from Node as N where N.id IN (:ids)",[ids:children]) }
		}
		
		if((params.name && params.name!='null') && (params.status && params.status!='null') && (params.nodetype && params.nodetype!='null')){
			params.nodetype = NodeType.get(params.nodetype.toLong())
			Node nodeInstance  = new Node(params)
			nodeInstance.dateCreated = new Date()
						
	        if (!nodeInstance.save(flush: true)) {
	            render(view: "create", model: [nodeInstance: nodeInstance])
	            return
	        }else{
				Date now = new Date()
				params.each{ key, val ->
					if (key.contains('att') && !key.contains('_filter') && !key.contains('_require')) {
						TemplateAttribute att = TemplateAttribute.get(key[3..-1].toInteger())
					   new TemplateValue(node:nodeInstance,templateattribute:att,value:val,dateCreated:now,dateModified:now).save(failOnError:true)
					}
				}
				
				if(parents){
					parents.each{ parent ->
						ChildNode parentNode = ChildNode.findByParentAndChild(parent,nodeInstance)
						if(!parentNode){
							parentNode = new ChildNode()
							parentNode.parent = parent
							parentNode.child = nodeInstance
							parentNode.save(flush: true)
						}
					}
				}
				
				if(children){
					children.each{ child ->
						ChildNode childNode = ChildNode.findByParentAndChild(nodeInstance,child)
						if(!childNode){
							childNode = new ChildNode()
							childNode.parent = nodeInstance
							childNode.child = child
							childNode.save(flush: true)
						}
					}
				}
				
				ArrayList nodes = [nodeInstance]
				def xml = xmlService.formatNodes(nodes)
				webhookService.postToURL( params.controller, xml.toString())
				
				flash.message = message(code: 'default.created.message', args: [message(code: 'node.label', default: 'Node'), nodeInstance.id])
		        redirect(action: "show", id: nodeInstance.id)
	        }
		}else{
			Node nodeInstance  = new Node()
			flash.message = 'Required fields not filled out. Please try again'
			render(view: "create", model: [nodeInstance: nodeInstance,parents:parents,children:children,params: params])
		}
    }

    def show() {
		def path = iconService.getLargeIconPath()
		def nodeTypeInstance = NodeType.get(params.id)
		
        def nodeInstance = Node.get(params.id)
		
		if(params.format){
			switch(params.format){
				case 'xml':
				case 'XML':
					ArrayList nodes = [nodeInstance]
					def xml = xmlService.formatNodes(nodes)
					render(text: xml, contentType: "text/xml")
					break;
			}
		}else{
			def criteria = ChildNode.createCriteria()
			def parents = criteria.list{
				eq("child", Node.get(params.id?.toLong()))
			}
			
			def criteria2 = ChildNode.createCriteria()
			def children = criteria2.list{
				eq ("parent", Node.get(params.id?.toLong()))
			}
			
			def tagList = nodeInstance.tags.split(',')
			
	        if (!nodeInstance) {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'node.label', default: 'Node'), params.id])
	            redirect(action: "list")
	            return
	        }

			[children:children,parents:parents,nodeInstance: nodeInstance,path:path,taglist:tagList]
		}
    }

    def edit() {
        def nodeInstance = Node.get(params.id)
		//def nodes = Node.findAll("from Node as N where N.id!=${params.id}") 
		def criteria = Node.createCriteria()
		def nodes = criteria.list{
			ne ("id", params.id?.toLong())
		}
		
        if (!nodeInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'node.label', default: 'Node'), params.id])
            redirect(action: "list")
            return
        }

		def parents = Node.executeQuery("select new map(N.id as id,N.name as name) from Node as N left join N.nodetype as NT left join NT.parents as NTP where NTP.child=${nodeInstance.nodetype.id} and (NTP.childCardinality<=(select count(*) from NodeType where id=NTP.parent.id) or NTP.childCardinality is null)")
		def children = Node.executeQuery("select new map(N.id as id,N.name as name) from Node as N left join N.nodetype as NT left join NT.children as NTP where NTP.parent=${nodeInstance.nodetype.id} and (NTP.parentCardinality<=(select count(*) from NodeType where id=NTP.child.id) or NTP.parentCardinality is null)")
		
        [parents:parents,children:children,nodes:nodes,nodeInstance: nodeInstance]
    }

    def update() {
		def parents
		if(params.parents){
			Long[] adults = Eval.me("${params.parents}")
			if(params.parents){ parents = Node.findAll("from Node as N where N.id IN (:ids) and N.id!=${params.id}",[ids:adults]) }
		}
		
		def children
		if(params.children){
			Long[] kinder = Eval.me("${params.children}")
			if(params.children){ children = Node.findAll("from Node as N where N.id IN (:ids) and N.id!=${params.id}",[ids:children]) }
		}
		
		if((params.name && params.name!='null') && (params.status && params.status!='null') && (params.nodetype && params.nodetype!='null')){
	        def nodeInstance = Node.get(params.id)
			Date now = new Date()
	        if (!nodeInstance) {
	            flash.message = message(code: 'default.not.found.message', args: [message(code: 'node.label', default: 'Node'), params.id])
	            redirect(action: "list")
	            return
	        }
	
	        if (params.version) {
	            def version = params.version.toLong()
	            if (nodeInstance.version > version) {
	                nodeInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
	                          [message(code: 'node.label', default: 'Node')] as Object[],
	                          "Another user has updated this Node while you were editing")
	                render(view: "edit", model: [nodeInstance: nodeInstance])
	                return
	            }
	        }
	
			nodeInstance.name = params.name
			nodeInstance.description = params.description
			nodeInstance.status = params.status
			nodeInstance.tags = params.tags
			nodeInstance.dateCreated = now
			nodeInstance.dateModified = now
			
	        if (!nodeInstance.save(flush: true)) {
	            render(view: "edit", model: [nodeInstance: nodeInstance])
	            return
	        }else{
				params.each{ key, val ->
					if (key.contains('att') && !key.contains('_filter') && !key.contains('_require')) {
						TemplateValue tval = TemplateValue.get(key[3..-1].toInteger())
						tval.value = val
						tval.dateCreated = now
						tval.dateModified = now
						tval.save(flush: true)
					}
				}
				flash.message = message(code: 'default.created.message', args: [message(code: 'node.label', default: 'Node'), nodeInstance.id])
		        redirect(action: "show", id: nodeInstance.id)
	        }
			render(view: "edit", model: [nodeList: Node.list(),nodeInstance: nodeInstance])
		}else{
			def nodeInstance = Node.get(params.id)
			flash.message = 'Required fields not filled out. Please try again'
			render(view: "edit", model: [parents:parents,children:children,nodeInstance: nodeInstance,params: params])
		}
    }

    def delete() {
		Node.withTransaction{ status ->
	        def nodeInstance = Node.get(params.id)
	        if (!nodeInstance) {
				println("delete: nodeinstance found")
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'node.label', default: 'Node'), params.id])
	            redirect(action: "list")
	            return
	        }
	
	        try {
	            nodeInstance.delete(flush: true)
				flash.message = message(code: 'default.deleted.message', args: [message(code: 'node.label', default: 'Node'), params.id])
				redirect(action: "list")
	        }catch (Exception e) {
				status.setRollbackOnly()
				flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'node.label', default: 'Node'), params.id])
	            redirect(action: "show", id: params.id)
	        }
		}
    }
	
	def getNodeParents = {
		def response = []
		if(params?.id.trim()){
			List atts = Node.executeQuery("select new map(N.id as id,N.name as name) from Node as N left join N.nodetype as NT left join NT.parents as NTP where NTP.child=${params.id.toLong()} and (NTP.childCardinality<=(select count(*) from NodeType where id=NTP.parent.id) or NTP.childCardinality is null)")
			atts.each(){
				response += [id:it.id,name:it.name];
			}
			render response as JSON
		}
	}
	

	def getNodeChildren = {
		def response = []
		if(params?.id.trim()){
			List atts = Node.executeQuery("select new map(N.id as id,N.name as name) from Node as N left join N.nodetype as NT left join NT.children as NTP where NTP.parent=${params.id.toLong()} and (NTP.parentCardinality<=(select count(*) from NodeType where id=NTP.child.id) or NTP.parentCardinality is null)")
			atts.each(){
				response += [id:it.id,name:it.name];
			}
			render response as JSON
		}
	}
	
	def getTemplateAttributes = {

			def response = []

			if(params.templateid){
				println("")
				List atts = []
				if(params.node){
					atts = TemplateValue.executeQuery("select new map(TV.id as tid,TV.value as templatevalue,TA.required as required,A.name as attributename,A.id as id,F.dataType as datatype,F.regex as filter) from TemplateValue as TV left join TV.templateattribute as TA left join TA.attribute as A left join A.filter as F where TA.template.id=${params.templateid} and TV.node.id=${params.node}");
				}else{
					atts = TemplateAttribute.executeQuery("select new map(A.id as id,TA.required as required,A.name as attributename,F.dataType as datatype,F.regex as filter) from TemplateAttribute as TA left join TA.attribute as A left join A.filter as F where TA.template.id=${params.templateid}");
				}
				atts.each(){
					response += [tid:it.tid,id:it.id,required:it.required,key:it.templatevalue,val:it.attributename,datatype:it.datatype,filter:it.filter];
				}
			}

			render response as JSON
	}
}
