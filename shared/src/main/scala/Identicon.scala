package identicon

import java.lang.Math
import java.util.Base64

trait Identicon {

  private def lrot(x: Int, c: Int): Int = (x << c) | (x >>> (32 - c))

  // NB: this implementation has a bug and does NOT correspond to the actual MD5
  // specification. It does however produce a hash that appears to be good
  // enough for generating identicons.
  private def md5(message: Array[Byte]): Array[Byte] = {
    val s = Array[Int](
      7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 5, 9, 14, 20,
      5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 4, 11, 16, 23, 4, 11, 16, 23, 4,
      11, 16, 23, 4, 11, 16, 23, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6,
      10, 15, 21
    )

    val k = Array[Int](
      0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf, 0x4787c62a,
      0xa8304613, 0xfd469501, 0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
      0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821, 0xf61e2562, 0xc040b340,
      0x265e5a51, 0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
      0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8,
      0x676f02d9, 0x8d2a4c8a, 0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
      0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70, 0x289b7ec6, 0xeaa127fa,
      0xd4ef3085, 0x04881d05, 0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
      0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92,
      0xffeff47d, 0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
      0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    )

    var a0 = 0x67452301
    var b0 = 0xefcdab89
    var c0 = 0x98badcfe
    var d0 = 0x10325476

    // number of 64-byte (512-bit) blocks that will be processed
    val nblocks = ((message.size + 8) >>> 6) + 1
    val bytes = new Array[Byte](nblocks << 6)

    Array.copy(message, 0, bytes, 0, message.size)
    bytes(message.size) = 0x80.toByte

    val bitLength: Long = message.size.toLong << 3
    bytes(bytes.size - 8) = (bitLength).toByte
    bytes(bytes.size - 7) = (bitLength >>> 8).toByte
    bytes(bytes.size - 6) = (bitLength >>> 16).toByte
    bytes(bytes.size - 5) = (bitLength >>> 24).toByte
    // bytes(bytes.size - 4) = (bitLength >>> 32).toByte
    // bytes(bytes.size - 3) = (bitLength >>> 40).toByte
    // bytes(bytes.size - 2) = (bitLength >>> 48).toByte
    // bytes(bytes.size - 1) = (bitLength >>> 56).toByte

    for (block <- 0 until nblocks) {
      val m = new Array[Int](16)
      var index = block << 6
      for (i <- 0 until 16) {
        m(i) = bytes(index) |
          (bytes(index + 1) << 8) |
          (bytes(index + 2) << 16) |
          (bytes(index + 3) << 24)
        index += 4
      }

      var a = a0
      var b = b0
      var c = c0
      var d = d0

      for (i <- 0 until 64) {
        var f = 0
        var g = 0
        if (i < 16) {
          f = (b & c) | ((~b) & d)
          g = i
        } else if (i < 32) {
          f = (d & b) | ((~d) & c)
          g = (5 * i + 1) & 0x0f
        } else if (i < 48) {
          f = b ^ c ^ d
          g = (3 * i + 5) & 0x0f
        } else {
          f = c ^ (b | (~d))
          g = (7 * i) & 0x0f
        }
        f = f + a + k(i) + m(g)
        a = d
        d = c
        c = b
        b = b + lrot(f, s(i))
      }
      a0 += a
      b0 += b
      c0 += c
      d0 += d
    }
    val digest = new Array[Byte](16)
    digest(0) = a0.toByte
    digest(1) = (a0 >>> 8).toByte
    digest(2) = (a0 >>> 16).toByte
    digest(3) = (a0 >>> 24).toByte
    digest(4) = b0.toByte
    digest(5) = (b0 >>> 8).toByte
    digest(6) = (b0 >>> 16).toByte
    digest(7) = (b0 >>> 24).toByte
    digest(8) = c0.toByte
    digest(9) = (c0 >>> 8).toByte
    digest(10) = (c0 >>> 16).toByte
    digest(11) = (c0 >>> 24).toByte
    digest(12) = d0.toByte
    digest(13) = (d0 >>> 8).toByte
    digest(14) = (d0 >>> 16).toByte
    digest(15) = (d0 >>> 24).toByte
    digest
  }

  def svg(name: String): String = {
    val hash = md5(name.getBytes("UTF-8"))
    val builder = new StringBuilder
    val color = {
      val r = hash(0) & 0xff
      val g = hash(1) & 0xff
      val b = hash(2) & 0xff
      f"#$r%02x$g%02x$b%02x"
    }
    val style = s"fill:$color;stroke:$color;stroke-width:0.05"
    builder ++= s"""<svg xmlns="http://www.w3.org/2000/svg" width="5" height="5">"""
    for (x <- 0 until 2) {
      for (y <- 0 until 5) {
        if (((hash(x) >>> y) & 0x01) != 0) {
          builder ++= s"""<rect x="$x" y="$y" width="1" height="1" style="$style"/>"""
          builder ++= s"""<rect x="${4 - x}" y="$y" width="1" height="1" style="$style"/>"""
        }
      }
    }
    for (y <- 0 until 5) {
      if (((hash(2) >>> y) & 0x01) != 0) {
        builder ++= s"""<rect x="2" y="$y" width="1" height="1" style="$style"/>"""
      }
    }
    builder ++= "</svg>"
    builder.result()
  }

  def url(name: String): String = {
    val b64 = Base64.getEncoder().encodeToString(svg(name).getBytes("UTF-8"))
    s"data:image/svg+xml;base64,$b64"
  }

}
