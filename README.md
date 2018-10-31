# Identicons

A tiny implementation of
[identicons](https://en.wikipedia.org/wiki/Identicon), similar to the
ones used by GitHub for new avatars.

## Dependencies

This library is totally self-contained, there are no 3rd party
dependencies. It is built and published for Scala on JVM, JS and
Native.

```sbt
"io.crashbox" %%% "identicon" % "<unreleased>"
```

## Usage

```scala
// generate identicon for user "admin" in SVG form
identicon.svg("admin") // = <svg width="5" height="5"><rect x="0" y="3" ...

// in base64 data url form
identicon.url("admin") // = data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0...

```

See the [online generator](https://jakob.odersky.com/identicon) for an
interactive preview. The generator is simply a static site that uses
the ScalaJS version of this library.
