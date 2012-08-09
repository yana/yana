package com.dtolabs

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.validation.FieldError

@TestFor(NodeController)
@Mock([Node, NodeType, Project, NodeValue, ChildNode, Webhook, Filter, Attribute, NodeAttribute])
class NodeControllerTests {


    void testIndex() {
        controller.index()
        assert "/node/list" == response.redirectedUrl
    }

    void testList() {
        defineBeans {
            nodeService(NodeService)
            webhookService(WebhookService)
            iconService(IconService)
        }

        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()
        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()
        Node node2 = new Node(name: 'node2', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()
        Node node3 = new Node(name: 'node3', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.project = project.name

        // model:	[nodeInstanceList: nodes, nodeInstanceTotal: totCount, path:path]

        /**
         * Run the controller action
         */
        def model = controller.list()

        assertEquals(3, model.nodeInstanceList.size())
        assertEquals(3, model.nodeInstanceTotal)
        assertTrue(model.nodeInstanceList.contains(node1))
        assertTrue(model.nodeInstanceList.contains(node2))
        assertTrue(model.nodeInstanceList.contains(node3))
    }


    void testList_asXml() {
        defineBeans {
            nodeService(NodeService)
            webhookService(WebhookService)
            iconService(IconService)
            xmlService(XmlService)
        }

        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()
        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()
        Node node2 = new Node(name: 'node2', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()
        Node node3 = new Node(name: 'node3', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.project = project.name
        params.format = "xml"

        /**
         * Run the controller action
         */
        controller.list()

        assertNotNull("Response did not contain XML", response.xml)
        assertEquals("Incorrect number of nodes:", 3, response.xml.node.size())
        assertEquals(node1.name, response.xml.node[0].@name.text())
        assertEquals(node2.name, response.xml.node[1].@name.text())
        assertEquals(node3.name, response.xml.node[2].@name.text())


    }

    void testCreate() {
        defineBeans {
            nodeService(NodeService)
            webhookService(WebhookService)
        }
        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        params.project = project.name

        /**
         * Run the controller action
         */
        def model = controller.create() // this action returns a map

        assertEquals("Incorrect response code.", 200, response.status)
        assertNotNull("nodeTypeList was not found in the model.", model.nodeTypeList)
        assertEquals(nodeType.name, model.nodeTypeList[0].name)
    }

    void testSave() {
        defineBeans {
            nodeService(NodeService)
            webhookService(WebhookService)
        }

        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        params.name = "node1"
        params.description = "desc"
        params.'nodetype.id' = nodeType.id
        params.tags = "tag1,tag2"
        params.project = project.name
        request.method = "POST"

        /**
         * Run the controller action
         */
        def model = controller.save()

        assertEquals("Incorrect node count: " + Node.count, 1, Node.count)
        assertEquals("Incorrect response code.", 302, response.status)
        assertEquals("/node/show/1", response.redirectedUrl)

        def node1 = Node.findByNameAndProject("node1", project)
        assertNotNull("The node1 instance not found after save operation.", node1)

    }

    void testSave_NotUnique() {
        defineBeans {
            nodeService(NodeService)
            webhookService(WebhookService)
        }

        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.name = "node1"
        params.description = "desc"
        params.'nodetype.id' = nodeType.id
        params.tags = "tag1,tag2"
        params.project = project.name
        request.method = "POST"

        /**
         * Run the controller action
         */
        controller.save()

        assertEquals("Incorrect node count: " + Node.count, 1, Node.count)
        assertEquals("Incorrect response code.", 200, response.status)
        assertEquals("Incorrect view.", "/node/create", view)
        assertNotNull("nodeInstance not found in model.", model.nodeInstance)
        /**
         * This is the critical test. Check if name has a field error
         */
        assert model.nodeInstance.errors.hasFieldErrors("name")
        FieldError err = model.nodeInstance.errors.getFieldError("name")
        def value = err.getRejectedValue()
        assertEquals("Incorrect rejected field value:", node1.name, value)
    }


    void testShow() {
        defineBeans {
            nodeService(NodeService)
            webhookService(WebhookService)
        }

        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.id = 1
        params.project = project.name

        /**
         * Run the controller action
         */
        controller.show()

        assertEquals("Incorrect view.", "/node/show", view)

        assertEquals(node1, model.nodeInstance)
    }

    void testShow_asXml() {
        defineBeans {
            nodeService(NodeService)
            webhookService(WebhookService)
        }

        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.id = 1
        params.project = project.name
        params.format = "xml"

        /**
         * Run the controller action
         */
        controller.show()

        assertNotNull("Response did not contain XML", response.xml)
        assertEquals("Incorrect number of nodes:", 1, response.xml.node.size())
        assertEquals(node1.name, response.xml.node[0].@name.text())
    }

    void testEdit() {

        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.id = 1
        params.project = project.name

        /**
         * Run the controller action
         */
        def model = controller.edit()

        /* model:
        [selectedParents:selectedParents,
         selectedChildren:selectedChildren,
         unselectedParents:unselectedParents,
         unselectedChildren:unselectedChildren,
         nodes:nodes,
         nodeInstance:nodeInstance]
          */

        assertNotNull("Node not found", model.nodeInstance)
        assertEquals("Incorrect node found", node1, model.nodeInstance)

    }


    void testUpdate() {
        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.id = 1
        params.project = project.name
        /**
         * Change these properties
         */
        params.tags = "tag3"
        params.description = "new description"

        /**
         * Run the controller action
         */
        controller.update()

        assertEquals("/node/show/1", response.redirectedUrl)
        def nodeInstance = Node.get(1)
        assertEquals(params.tags, nodeInstance.tags)
        assertEquals(params.description, nodeInstance.description)

    }

    void testUpdate_withAttributes() {
        Project project = new Project(name: 'test1', description: 'desc').save()
        Filter filter = new Filter(project: project, dataType: "String", regex: ".*").save()
        Attribute arch = new Attribute(name:  "arch", project:  project, filter: filter).save()
        Attribute repo = new Attribute(name:  "repo", project:  project, filter: filter).save()
        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()
        NodeAttribute attr1 = new NodeAttribute(attribute: arch, nodetype: nodeType, required: false).save()
        NodeAttribute attr2 = new NodeAttribute(attribute: repo, nodetype: nodeType, required: false).save()
        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()
        def nv1 = new NodeValue(node: node1, nodeattribute:  attr1, value: "sparc").save()
        def nv2 = new NodeValue(node: node1, nodeattribute:  attr2, value: "ftp://localhost/resource").save()

        params.id = node1.id
        params.project = project.name
        params.att1 = "noarch"
        params.att2 = "http://localhost/resource"

        /**
         * Change these properties
         */
        params.tags = "tag3"
        params.description = "new description"

        /**
         * Run the controller action
         */
        controller.update()

        assertEquals("/node/show/1", response.redirectedUrl)
        def nodeInstance = Node.get(1)
        assertEquals(params.tags, nodeInstance.tags)
        assertEquals(params.description, nodeInstance.description)
        assertEquals(params.att1, nv1.value )
        assertEquals(params.att2, nv2.value )


    }


    void testDelete() {
        Project project = new Project(name: 'test1', description: 'desc').save()

        NodeType nodeType = new NodeType(project: project,
                name: "TypeA",
                description: "test node type A description",
                image: "Node.png").save()

        Node node1 = new Node(name: 'node1', description: 'desc', tags: 'tag1,tag2',
                project: project, nodetype: nodeType).save()

        params.id = 1
        params.project = project.name

        /**
         * Run the controller action
         */
        controller.delete()

        assertEquals("/node/list", response.redirectedUrl)
        assertNull("Node not deleted", Node.get(1))
    }
}
