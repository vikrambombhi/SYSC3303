### How to compile
The programs should all already be compiled you can find them in the `build` directroy.
Follow these steps if you want to recompile the programs.

1. Open a terminal (powershell if on windows)
2. Change directory to the project `cd /path/to/assignment`
3. Compile the programs with `make`

### How to run
##### Windows
1. Open 3 terminals (powershell if on windows)
2. Navigate to the build folder with `cd /path/to/assignment/build` in all terminals
3. Run the Server on the first terminal with `java Server`
4. Run the Intermediate on the second terminal with `java Intermediate`
5. Run the Client on the third terminal with `java Client`

##### Linux/MacOS
1. Open 3 terminals
2. Navigate to the build folder with `cd /path/to/assignment` in all terminals
3. Run with using command `./run`, depending on your distro you might need to do `sudo ./run` if you see a error with perrmissions


## Explanation
### Client
==============================
The client sends 10 read/write requests and waits for the response between each one. After the client sends 1 invalid request and waits endlessly for the response.

### Intermediate
==============================
The intermediatary forwards all requests from the client to the server and all responses from the server back to the client.

### Server
==============================
The server accepts all requests, verifies them, and responds appropritly. In the case of a invalid request the server exits.
