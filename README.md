# REST Server for DXRAM
by Julien Bernhart

## Build
Clone the project and execute

```
./gradlew jar
```

## Install
Copy the dxrest-x.x.jar from build/libs to the dxapp folder of your dxram installation.

To autostart the rest server add this to yout dxram configuration:

```json
"ApplicationServiceConfig": {
"m_autoStart": [
  {
    "m_className": "de.hhu.bsinfo.dxapp.rest.RestServerApplication",
    "m_args": "8009",
    "m_startOrderId": 0
  }
],
"m_classConfig": "de.hhu.bsinfo.dxram.app.ApplicationServiceConfig"
},
```

## Usage
The server is running on port 8009. To see all available commands visit http://<server_ip>:8009

It is recommended to use a rest client like Insomnia. (https://insomnia.rest)

## Commands
Use this commands with this URL: http://<server_ip>:8009/command

Commands with parameters require a json body with the expected parameters.

Optional parameters are *cursive*.

| Command | Type | Parameter | Info | Notes | 
| ------- | ------ |--------- | ---- | ---- |
| applist | GET | - | lists running applications |
| apprun | PUT | nid, appname | starts a dxapp on a remote peer |
| chunkcreate | PUT |  nid, size | creates a new chunk on remote peer |
| chunkdump | PUT |  name, cid | create chunkdump with specified filename |
| chunkget | GET |  cid, type | get chunk, supported types: str,byte,short,int,long |
| chunklist | GET | nid | get chunklist for a specific remote node |
| chunkput | PUT | cid, type, data | put data to chunk | |
| chunkremove | PUT | cid | remove specific chunk |
| loginfo | GET | nid | prints the log utilization of given peer | testing |
| lookuptree | GET | nid | get the lookuptree of a specified node | testing |
| metadata | GET | *nid* | get summary of all or one superper's metadata |
| monitoring | GET | nid | get monitoring data of given peer | testing |
| namget | GET | name | get chunk by name |
| namelist |GET |  - | get namelist |
| namreg | PUT | cid, name | register chunk with name |
| nodeinfo | GET | nid |  get information about a node in the network |
| nodelist | GET | - | list all nodes |
| statsprint | GET | - | debug information | html site with autorefresh |
| barrierstatus | GET | bid | get status of a barrier |
| barrieralloc | PUT | size | alloc barrier with given size |
| barriersignon | PUT | bid, *data* | sign into an existing barrier for synchronization |
| barrierfree | PUT | bid | free an allocated barrier |
| barriersize | PUT | bid, size | change the size of an existing barrier |
| loggerlevel | PUT | level, *nid* | change the output level of the logger |
| nodeshutdown | PUT | nid, *kill* | shutdown dxram node |

## Example

URL: http://localhost:8009/nodeinfo

Request body:

```json
{
  "nid":"b1bd"
}
```

Result:

```json
{
  "nid": "0xB1BD",
  "role": "peer",
  "address": "/127.0.0.1:22222",
  "capabilities": "[STORAGE]"
}
```

