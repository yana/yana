package org.yana

import grails.plugins.springsecurity.Secured

@Secured(['permitAll'])
class ErrorsController {
    def error404 = {}
}
