package org.yana

class Node {

    static searchable = {
        nodetype component:true
        project component:true
        except=['version','tags']
        name index: 'not_analyzed', name: 'nameSort'
        name index: 'analyzed'
        tagsTokens name: 'tags'
    }
	
	static mappedBy = [
            // maps this Node as the 'parent' field of ChildNodes in the children set.
            children: 'parent',
            // maps this Node as the 'child' field of ChildNodes in the parents set.
            parents: 'child'
    ]
	static hasMany = [nodeValues:NodeValue,children:ChildNode,parents:ChildNode]
	
    String name
    String description
	String tags
	NodeType nodetype
    Project project;
    Date dateCreated
    Date lastUpdated
    static transients = ['tagsTokens','nameTokens']

    static constraints = {
        name(blank:false, unique: ['project', 'nodetype'])
        description(blank:true, nullable:true)
        tags(nullable:true)
		nodetype(nullable:false)
        project(nullable: false)
    }
    def String getTagsTokens(){
        return tags?tags.split(/[,\s]+/).join(' ').toString(): null
    }
    def String getNameTokens(){
        return name.split(/\./).join(' ')
    }

    def String toString() {
        return "${name}[${nodetype.name}]"
    }

   // A dynamic (like?) find method
   static Set<Node> findAllTagsByName(String name)  {
       return Node.withCriteria {
		   like('tags', "%${name}%")
       }
   }

   // A dynamic (like?) find method
   static Set<Node> findAllByNameLikeAndTagsByName(String nameLike, String tagName)  {
       return Node.withCriteria {
            ilike('name',nameLike)
            like ('tags',"%${tagName}%")
       }
   }

    Map toMap() {
        def map = [
                id: this.id,
                name: this.name,
                description: this.description,
                tags: this.tags,
                type: this.nodetype.name,
                typeId: this.nodetype.id
        ]

        if (this.nodeValues) {
            map.attributes = []
            this.nodeValues.each { NodeValue attr ->
                map.attributes << attr.toMap()
            }
        }
        if (this.children) {
            map.children = []
            this.children.each {ChildNode child ->
                map.children << child.toMap()
            }
        }
        if (this.parents) {
            map.parents = []
            this.parents.each {ChildNode child ->
                map.parents << child.toMap()
            }
        }

        return map
    }

}

