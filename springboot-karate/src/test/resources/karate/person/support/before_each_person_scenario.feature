# This is a re-usable feature that will be called from other features. We prevent it from being picked up by our runner
# by annotating it with @ignore.
#
# This feature will persist a predefined set of persons before each scenario will be run. This is achieved by:
# - defining the set of persons as a table with the columns firstName and lastName,
# - call the add_person feature passing the table of persons; karate will then automatically iterate over the persons,
#   in the table and call the feature for each person,
# - capture all the responses for each call,
# - verify the type of each property of all the responses,
# - verify that the set of responses contain the expected list of first names.
#
# So this is an example of:
# - using a table,
# - using a collection of arguments with automatic iteration over the collection,
# - capturing a set of responses,
# - performing more complex assertions on a set of responses.
@ignore
Feature: Setup the environment before a person REST API scenario is executed

  Background:
    * table persons
      | firstName | lastName |
      | 'Jan'  |   'Janssen' |
      | 'Pieter' | 'Pietersen' |
      | 'Erik' |  'Eriksen' |

  Scenario:
    * def featureFile = supportFolderPath + 'add_person.feature'
    * def result = call read(featureFile) persons
    * def created = $result[*].response
    * match each created == { id: '#number', firstName: '#string', lastName: '#string' }
    * match created[*].firstName contains only ['Jan', 'Pieter', 'Erik']
