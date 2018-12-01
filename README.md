# Identicons<img align="right" src="sample.svg" height="150px" style="padding-left: 20px"/>

A tiny implementation of
[identicons](https://en.wikipedia.org/wiki/Identicon), similar to the
ones used by GitHub for new avatars.

If you are interested in the workings of identicons, check out this
[excellent blog
post](https://barro.github.io/2018/02/avatars-identicons-and-hash-visualization/)
by Jussi Judin.

## Dependencies

This library is totally self-contained, there are no 3rd party
dependencies. It is built and published for Scala on JVM, JS and
Native.

[![Download](https://img.shields.io/maven-central/v/io.crashbox/identicon_2.12.svg)](http://search.maven.org/#search|ga|1|io.crashbox%20identicon-)


```sbt
"io.crashbox" %%% "identicon" % "0.1.0"
```

## Usage

```scala
// generate identicon for user "admin" in SVG form
identicon.svg("admin") // = <svg width="5" height="5"><rect x="0" y="3" ...

// in base64 data url form
identicon.url("admin") // = data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0...

```

See the [online generator](https://jodersky.github.io/identicon) for an
interactive preview. The generator is simply a static site that uses
the ScalaJS version of this library.
