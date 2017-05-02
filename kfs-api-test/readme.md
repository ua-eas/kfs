Running the tests require a command line parameter defined that gives the location of a properties file.

----
mvn test -Dtest_properties=./test.properties
----

The properties file should contain:

* day_token - Valid authentication token for user day
* khuntley_token - Valid authentication token for user khuntley
* butt_token - Valid authentication token for user butt
* eagle_token - Valid authentication token for user eagle
* test_url - URL to application test.  Should include protocol, host and context with no trailing slash
