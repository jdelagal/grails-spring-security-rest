package com.odobo.grails.plugin.springsecurity.rest

import grails.plugins.rest.client.RestResponse

class RestTokenValidationFilterSpec extends AbstractRestSpec {

    void "accessing a secured controller without token returns 400"() {
        when:
        def response = restBuilder.get("${baseUrl}/secured")

        then:
        response.status == 400
    }

    void "accessing a secured controller with wrong token, returns 401"() {
        when:
        def response = restBuilder.get("${baseUrl}/secured") {
            header 'X-Auth-Token', 'whatever'
        }

        then:
        response.status == 401

    }

    void "accessing a public controller without token returns 302"() {
        when:
        def response = restBuilder.post("${baseUrl}/public")

        then:
        response.status == 302
    }

    void "accessing a public controller with wrong token, returns 302"() {
        when:
        def response = restBuilder.post("${baseUrl}/public") {
            header 'X-Auth-Token', 'whatever'
        }

        then:
        response.status == 302

    }

    void "a valid user can access the secured controller"() {
        given:
        RestResponse authResponse = sendCorrectCredentials()
        String token = authResponse.json.token

        when:
        def response = restBuilder.get("${baseUrl}/secured") {
            header 'X-Auth-Token', token
        }

        then:
        response.status == 200
        response.text == 'jimi'
    }

    void "role restrictions are applied when user does not have enough credentials"() {
        given:
        RestResponse authResponse = sendCorrectCredentials()
        String token = authResponse.json.token

        when:
        def response = restBuilder.get("${baseUrl}/secured/superAdmin") {
            header 'X-Auth-Token', token
        }

        then:
        response.status == 403
    }

}
