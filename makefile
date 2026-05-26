SRC_CRY = src/aes/*.java src/sha/*.java src/chacha/*.java
SRC_AES = src/aes/*.java
SRC_SHA = src/sha/*.java
SRC_CHA = src/chacha/*.java

SRC_TEST = testing/*.java
SRC_SUITE = testing/Test*.java
SRC_VERBOSE = testing/TestUtils.java testing/Verbose*.java

JC = javac -d out
JR = java -cp out

all: compile

compile:
	@mkdir -p out
	@$(JC) $(SRC_CRY) $(SRC_TEST)

clean:
	@rm -rf out

# TEST SUITES

test: compile
	@$(JR) testing.Test

test-aes: compile
	@$(JR) testing.TestAES256

test-sha: compile
	@$(JR) testing.TestSHA256

test-chacha: compile
	@$(JR) testing.TestChaCha20

# VERBOSITY

verbose: compile
	@$(JR) testing.Verbose
	
verbose-aes: compile
	@$(JR) testing.VerboseAES256

verbose-sha: compile
	@$(JR) testing.VerboseSHA256

verbose-chacha: compile
	@$(JR) testing.VerboseChaCha20
