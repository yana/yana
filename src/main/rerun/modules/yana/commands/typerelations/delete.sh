#
# ACTION
#
#   delete
#
# DESCRIPTION
#
#   delete a node type relationship
#

curl --silent --fail --request DELETE \
    ${YANA_URL}/api/nodeTypeRelationship/xml/${ID}?project=${PROJECT} \
    -o ${response} --cookie ${cookie}

# ------------------------------

return $?

# Done

