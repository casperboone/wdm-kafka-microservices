# Web-scale Data Management on Kafka - Locust Load Testing

Requirements:
* Python 3

## Usage Instructions
Install locust using 
```
pip install locust
```

Run
```
locust --host=http://localhost:8080
```

Go to `http://localhost:8089/` and start the load test.

To run on a remote server:

go to `load-test-runner` and replace the host with
```
HOST="SERVER_ADDRESS"
```
And run the script.