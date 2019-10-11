# This is a re-usable feature that will be called from other features. We prevent it from being picked up by our runner
# by annotating it with @ignore.
#
# This feature will delete all persons before each scenario will be run. This is achieved by:
# - issuing an REST API call to get a list of all existing the persons,
# - call the delete_person feature passing the list of existing persons; karate will then automatically iterate over the persons,
#   in the list and call the feature for each person.
#
# Note that the delete_person feature already validates the response so we omitted response validation in this scenario.
# So this is an example of:
# - using a REST API result for further processing,
# - using a collection of arguments with automatic iteration over the collection.
@ignore
Feature: cleanup the environment after a person REST API scenario is executed

  Background:
    * url testBaseUrl + '/api/person'

  Scenario:
    When method GET
    Then status 200

    * def allPersons = $result[*].response.items
    * call read('delete_person.feature') allPersons
