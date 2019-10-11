# This is a re-usable feature that will be called from other features. We prevent it from being picked up by our runner
# by annotating it with @ignore.
#
# This feature will call the delete-person REST API to delete a person and we will use it to empty the database
# after each scenario.
#
# This feature has one scenario and it will be called with a person instance as argument. The id property value of
# this instance is used to build the request path that we will send to the the REST API.
#
# So this is an example of a feature using arguments!
@ignore
Feature: common reusable feature to delete a person

  Background:
    * url testBaseUrl + '/api/person'

  Scenario:
    #* print __arg
    #* print 'ID=' + id
    Given path '/' + id
    When method delete
    Then status 202
