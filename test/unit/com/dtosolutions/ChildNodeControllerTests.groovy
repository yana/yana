package com.dtosolutions



import org.junit.*
import grails.test.mixin.*

@TestFor(ChildNodeController)
@Mock(ChildNode)
class ChildNodeControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'

	  Date now = new Date()
	  mockDomain(NodeType, [new NodeType(id:1,version:1,name:'Server',dateCreated:now)])
	  NodeType server = NodeType.get(1)
	  mockDomain(Node, [new Node(id:1,version:1,name:'Parent',description:'this is a node',status:Status.IMP,importance:Importance.MED,tags:'this,is,a,tag',nodetype:server,dateCreated:now)])
	  Node parent = Node.get(1)
	  mockDomain(Node, [new Node(id:2,version:1,name:'Parent',description:'this is a node',status:Status.IMP,importance:Importance.MED,tags:'this,is,a,tag',nodetype:server,dateCreated:now)])
	  Node child = Node.get(2)
	  
	  params["id"] = 1
	  params["version"] = 1
      params["parent"] = parent
	  params["child"] = child

	  
    }

    void testIndex() {
        controller.index()
        assert "/childNode/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.childNodeInstanceList.size() == 0
        assert model.childNodeInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.childNodeInstance != null
    }

    void testSave() {
        controller.save()

        assert model.childNodeInstance != null
        assert view == '/childNode/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/childNode/show/1'
        assert controller.flash.message != null
        assert ChildNode.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/childNode/list'


        populateValidParams(params)
        def childNode = new ChildNode(params)

        assert childNode.save() != null

        params.id = childNode.id

        def model = controller.show()

        assert model.childNodeInstance == childNode
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/childNode/list'


        populateValidParams(params)
        def childNode = new ChildNode(params)

        assert childNode.save() != null

        params.id = childNode.id

        def model = controller.edit()

        assert model.childNodeInstance == childNode
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/childNode/list'

        response.reset()


        populateValidParams(params)
        def childNode = new ChildNode(params)



		
		
		if(childNode.save()){
			
			assert childNode.save(flush:true) != null
			
			//FIX
			controller.update()
			params.id = childNode.id
			assert response.redirectedUrl == "/childNode/show/$childNode.id"
			assert flash.message != null
		}else{
			// test invalid parameters in update
			//TODO: add invalid values to params object
        	assert view == "/childNode/edit"
			assert model.childNodeInstance != null
		}

        controller.update()
        childNode.clearErrors()
        populateValidParams(params)
		
		
		
		
		
		
		
        //test outdated version number
        response.reset()
        childNode.clearErrors()

        populateValidParams(params)
        params.id = childNode.id
        params.version = -1
        controller.update()

        assert view == "/childNode/edit"
        assert model.childNodeInstance != null
        assert model.childNodeInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/childNode/list'

        response.reset()

        populateValidParams(params)
        def childNode = new ChildNode(params)

        assert childNode.save() != null
        assert ChildNode.count() == 1

        params.id = childNode.id

        controller.delete()

        assert ChildNode.count() == 0
        assert ChildNode.get(childNode.id) == null
        assert response.redirectedUrl == '/childNode/list'
    }
}