# mozzarella

Function style minetest/minecraft objects generator.

## How To

_TODO_

## Additional minecraft requirements

* Spigot server
* Spigot server plugings:
  * JSONAPI, and its requirements:
  * ProtocolLib
  * Vault
* Minecraft client


## Minetest monitor

Install libpcap:

```
brew install libpcap
```

or 

```
yum install libpcap-devel
```

or 

```
apt-get install libpcap-dev
```

Run monitor:

```
./gradlew minetest-monitor:run
```


Start a minetest server at localhost:30000.

Start a minetest client & connect to the local server.

## License

Apache License 2.0
