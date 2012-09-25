package org.yana

import org.codehaus.groovy.grails.commons.GrailsClass
import org.yana.springacl.ProjectAccess
import org.yana.springacl.DefaultProjectAccess
import org.yana.Project
import org.yana.ProjectService
import org.springframework.context.MessageSource

class ProjectAccessFilters {
    def dependsOn = [ProjectFilters]
    def filters = {

        projectAccessPermission(controller: '*', controllerExclude: 'project|login|logout|errors|user|role', action: '*') {
            before = {
                if (params.project) {
                    //default project access level, if undeclared in the controller is 'read'
                    def GrailsClass ctype = grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName)
                    def ProjectAccess.Level alevel = ctype.getClazz().getAnnotation(DefaultProjectAccess)?.value()

                    if (!alevel) {
                        return true
                    }

                    def Project project = applicationContext.getBean(ProjectService).findProject(params.project)
                    if (!project) {
                        flash.message = applicationContext.getBean(MessageSource).getMessage('default.not.found.message',
                                                                                               ['Project', params.project] as Object[],
                                                                                               "Project {0} was not found", request.locale)
//                        render(status: HttpServletResponse.SC_NOT_FOUND)
                        redirect(controller: 'errors',action: 'error404')
                        return false
                    }

                    //look for method annotation
                    def cmethod = ctype.getClazz().getDeclaredMethod(actionName)
                    if (cmethod && cmethod.getAnnotation(ProjectAccess)) {
                        alevel = cmethod.getAnnotation(ProjectAccess).value()
                    }

                    if (alevel) {
                        switch (alevel) {
                            case ProjectAccess.Level.architect:
                                if (!applicationContext.getBean(ProjectService).authorizedArchitectPermission(project)) {
                                    //note: the method call will throw exception if it fails, this clause
                                    //is mostly for testing purposes
                                    flash.message = applicationContext.getBean(MessageSource).getMessage('springSecurity.denied.message',
                                                                                                         null,request.locale)
                                    redirect(controller: 'login', action: 'denied')
                                    return false
                                }
                                break;
                            case ProjectAccess.Level.operator:
                                if (!applicationContext.getBean(ProjectService).authorizedOperatorPermission(project)) {
                                    //note: the method call will throw exception if it fails, this clause
                                    //is mostly for testing purposes
                                    flash.message = applicationContext.getBean(MessageSource).getMessage('springSecurity.denied.message',
                                                                                                         null, request.locale)
                                    redirect(controller: 'login', action: 'denied')
                                    return false
                                }
                                break;
                        }
                    }
                }
            }
        }
    }
}
