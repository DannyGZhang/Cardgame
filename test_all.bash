#!/usr/bin/env bash
#
# Sample usage:
#   ./test_all.bash start stop
#   start and stop are optional
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
# When not in Docker
#: ${HOST=localhost}
#: ${PORT=7000}

# When in Docker
# shellcheck disable=SC2223
: ${HOST=localhost}
# shellcheck disable=SC2223
: ${PORT=8080}

#array to hold all our test data ids
allTournamentIds=()
allClientIds=()

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  # shellcheck disable=SC2155
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && ((${#result} > 3)) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]; then
    if [ "$httpCode" = "200" ]; then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
    echo "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
    echo "- Failing command: $curlCmd"
    echo "- Response Body: $RESPONSE"
    exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]; then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

#have all the microservices come up yet?
function testUrl() {
  # shellcheck disable=SC2124
  url=$@
  if curl $url -ks -f -o /dev/null; then
    echo "Ok"
    return 0
  else
    echo -n "not yet"
    return 1
  fi
}

#prepare the test data that will be passed in the curl commands for posts and puts
function setupTestdata() {

  #CREATE SOME PURCHASE ORDER TEST DATA - THIS WILL BE USED FOR THE POST REQUEST
  #all use clientId c3540a89-cb47-4c96-888e-ff96708db4d8

  body='{
     "cardGame":"80c063e1-2cea-4145-87e4-aafbc5854795",
     "entryCost": 5.00,
     "winner" : "a7b6bfc7-3904-4b0e-a684-73a0a1dea244",
     "location":"c293820a-d989-48ff-8410-24062a69d99e",
     "results": [
         {
             "clientId": "cb9e59da-229e-4eb5-b98e-15ff67b3930d",
             "victories": 1,
             "defeats": 3,
             "draws": 0
         },
         {
             "clientId": "a7b6bfc7-3904-4b0e-a684-73a0a1dea244",
             "victories": 3,
             "defeats": 1,
             "draws": 0
         }
     ]
 }'
  recreateTournament 1 "$body"

  body='    {
                 "storeId":"c293820a-d989-48ff-8410-24062a69d99e",
                 "firstName": "Dylan",
                 "lastName": "Girardini",
                 "totalBought": 1377.37,
                 "email": "hib@ox.ac.uk",
                 "phoneNumber": "285-342-7049"
             }'
  recreateClient 1 "$body"

}

#USING PURCHASE ORDER TEST DATA - EXECUTE POST REQUEST
function recreateTournament() {
  local testId=$1
  local aggregate=$2

  #create the purchaseorder aggregates and record the generated purchaseOrderIds
  tournamentId=$(curl -X POST http://$HOST:$PORT/api/lab2/v1/tournaments -H "Content-Type:
    application/json" --data "$aggregate" | jq '.tournamentId')
  allTournamentIds[$testId]=$tournamentId
  echo "Added tournament Aggregate with tournament Id: ${allTournamentIds[$testId]}"
}

function recreateClient() {
  local testId=$1
  local aggregate=$2

  #create the purchaseorder aggregates and record the generated purchaseOrderIds
  clientId=$(curl -X POST http://$HOST:$PORT/api/lab2/v1/stores/clients -H "Content-Type:
    application/json" --data "$aggregate" | jq '.clientId')
  allClientIds[$testId]=$clientId
  echo "Added tournament Aggregate with tournament Id: ${allClientIds[$testId]}"
}

#don't start testing until all the microservices are up and running
function waitForService() {
  # shellcheck disable=SC2124
  url=$@
  echo -n "Wait for: $url... "
  n=0
  until testUrl $url; do
    n=$((n + 1))
    if [[ $n == 100 ]]; then
      echo " Give up"
      exit 1
    else
      sleep 6
      echo -n ", retry #$n "
    fi
  done
}

#start of test script
set -e

echo "HOST=${HOST}"
echo "PORT=${PORT}"

# shellcheck disable=SC2199
if [[ $@ == *"start"* ]]; then
  echo "Restarting the test environment..."
  echo "$ docker-compose down"
  docker-compose down
  echo "$ docker-compose up -d"
  docker-compose up -d
fi

waitForService curl -X DELETE http://$HOST:$PORT/api/lab2/v1/stores/clients/2df29d30-4dd9-49a6-ad0e-c5810677f522

setupTestdata

echo -e "\nTest 1: Verify that a normal get by id of earlier posted tournament works"
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/tournaments/${allTournamentIds[1]} -s"
# shellcheck disable=SC2046
assertEqual ${allTournamentIds[1]} $(echo $RESPONSE | jq .tournamentId)
# shellcheck disable=SC2046
assertEqual "5" $(echo $RESPONSE | jq ".entryCost")
assertEqual "3" $(echo $RESPONSE | jq ".results[0].defeats")

echo -e "\nTest 2: Verify that a get all tournaments works"
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/tournaments -s"
assertEqual 3 $(echo $RESPONSE | jq ". | length")

echo -e "\nTest 3: Verify that an update of an earlier posted tournament works"
body='{
      "cardGame":"80c063e1-2cea-4145-87e4-aafbc5854795",
      "entryCost": 5.00,
      "winner" : "a7b6bfc7-3904-4b0e-a684-73a0a1dea244",
      "location":"c293820a-d989-48ff-8410-24062a69d99e",
      "results": [
          {
              "clientId": "cb9e59da-229e-4eb5-b98e-15ff67b3930d",
              "victories": 2,
              "defeats": 2,
              "draws": 0
          },
          {
              "clientId": "a7b6bfc7-3904-4b0e-a684-73a0a1dea244",
              "victories": 3,
              "defeats": 1,
              "draws": 0
          }
      ]
  }}'
assertCurl 200 "curl -X PUT http://$HOST:$PORT/api/lab2/v1/tournaments/${allTournamentIds[1]} -H \"Content-Type: application/json\" -d '${body}' -s"
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/tournaments/${allTournamentIds[1]} -s"
# shellcheck disable=SC2046
assertEqual ${allTournamentIds[1]} $(echo $RESPONSE | jq .tournamentId)
# shellcheck disable=SC2046
assertEqual "2" $(echo $RESPONSE | jq ".results[0].defeats")

echo -e "\nTest 4: Verify that post for tournament works"
body='{
      "cardGame":"80c063e1-2cea-4145-87e4-aafbc5854795",
      "entryCost": 5.00,
      "winner" : "7b4383c0-26a9-46b7-a604-c147f596da43",
      "location":"c293820a-d989-48ff-8410-24062a69d99e",
      "results": [
              {
                  "clientId": "cb4d6c99-a001-4ce9-909e-c21aab4eb9f2",
                  "victories": 1,
                  "defeats": 3,
                  "draws": 0
              },
              {
                  "clientId": "7b4383c0-26a9-46b7-a604-c147f596da43",
                  "victories": 3,
                  "defeats": 1,
                  "draws": 0
              }
          ]
  }}'
assertCurl 201 "curl -X POST http://$HOST:$PORT/api/lab2/v1/tournaments -H \"Content-Type: application/json\" -d '${body}' -s"
echo -e "Check that a new tournament was added : "
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/tournaments -s"
assertEqual 4 $(echo $RESPONSE | jq ". | length")

echo -e "\nTest 5: Verify that delete works for tournament"
assertCurl 204 "curl -v -X DELETE http://$HOST:$PORT/api/lab2/v1/tournaments/${allTournamentIds[1]}"

echo -e "Check that a the tournament was removed : "
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/tournaments -s"
assertEqual 3 $(echo $RESPONSE | jq ". | length")




echo -e "\nClient Test 1: Verify that a normal get by id of earlier posted client works"
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/stores/clients/${allClientIds[1]} -s"
# shellcheck disable=SC2046
assertEqual ${allClientIds[1]} $(echo $RESPONSE | jq .clientId)
# shellcheck disable=SC2046
assertEqual "\"Dylan\"" $(echo $RESPONSE | jq ".firstName")
assertEqual "\"Girardini\"" $(echo $RESPONSE | jq ".lastName")

echo -e "\nClient Test 2: Verify that a get all tournaments works"
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/stores/clients -s"
assertEqual 200 $(echo $RESPONSE | jq ". | length")



echo -e "\nClient Test 3: Verify that post for client works"
body='{"storeId":"c293820a-d989-48ff-8410-24062a69d99e","firstName": "Dylan","lastName":"Brassard", "email":"grussi3@un.org","totalBought":1000,"phoneNumber": "240-715-5145"}'
assertCurl 201 "curl -X POST http://$HOST:$PORT/api/lab2/v1/stores/clients -H \"Content-Type: application/json\" -d '${body}' -s"
echo -e "Check that a new client was added : "
assertCurl 200 "curl http://$HOST:$PORT/api/lab2/v1/stores/clients -s"
assertEqual 201 $(echo $RESPONSE | jq ". | length")






# shellcheck disable=SC2199
if [[ $@ == *"stop"* ]]; then
  echo "We are done, stopping the test environment..."
  echo "$ docker-compose down"
  docker-compose down
fi
