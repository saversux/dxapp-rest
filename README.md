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

```xml
"ApplicationServiceConfig": {
"m_autoStart": [
    {
        "m_className": "de.hhu.bsinfo.dxapp.rest.RestServerApplication",
        "m_args": "123",
        "m_startOrderId": 0
    }
],
"m_classConfig": "de.hhu.bsinfo.dxram.app.ApplicationServiceConfig"
},
```
