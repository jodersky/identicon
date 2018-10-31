package identicon

import java.lang.Math
import java.util.Base64

trait Identicon {

  private def lrot(x: Int, c: Int) = (x << c) | (x >>> (32 - c))

  // NB: this implementation has a bug and does NOT correspond to the actual MD5
  // specification. It does however produce a hash that appears to be good
  // enough for generating identicons.
  private def md5(in: IndexedSeq[Byte]): Array[Byte] = {
    var s = Array[Int](
      7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 5, 9, 14, 20,
      5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 4, 11, 16, 23, 4, 11, 16, 23, 4,
      11, 16, 23, 4, 11, 16, 23, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6,
      10, 15, 21
    )
    var K = Array[Int](
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

    // 1 = extra "0b1000000" byte
    val zeroBytes = Math.floorMod(61 - (in.size + 1), 64)
    val data = new Array[Byte](in.size + 1 + zeroBytes + 4) // 4 = length
    for ((byte, i) <- in.zipWithIndex) {
      data(i) = byte
    }
    data(in.size) = (1 << 7).toByte
    data(in.size + 1 + zeroBytes) = ((in.size >>> 24) & 0xff).toByte
    data(in.size + 1 + zeroBytes + 1) = ((in.size >>> 16) & 0xff).toByte
    data(in.size + 1 + zeroBytes + 2) = ((in.size >>> 8) & 0xff).toByte
    data(in.size + 1 + zeroBytes + 3) = ((in.size) & 0xff).toByte

    for (chunk <- 0 until data.size / 64) {
      val M = for (i <- 0 until 16) yield {
        (data(chunk * 64 + i * 4) << 24) |
          (data(chunk * 64 + i * 4 + 1) << 16) |
          (data(chunk * 64 + i * 4 + 2) << 8) |
          data(chunk * 64 + i * 4 + 3)
      }

      var A = a0
      var B = b0
      var C = c0
      var D = d0

      for (i <- 0 until 64) {
        var F = 0
        var g = 0
        if (0 <= i && i <= 15) {
          F = (B & C) | ((~B) & D)
          g = i
        } else if (16 <= i && i <= 31) {
          F = (D & B) | ((~D) & C)
          g = (5 * i + 1) % 16
        } else if (32 <= i && i <= 47) {
          F = B ^ C ^ D
          g = (3 * i + 5) % 16
        } else if (48 <= i && i <= 63) {
          F = C ^ (B | (~D))
          g = (7 * i) % 16
        }
        F = F + A + K(i) + M(g)
        A = D
        D = C
        C = B
        B = B + lrot(F, s(i))
      }
      a0 = a0 + A
      b0 = b0 + B
      c0 = c0 + C
      d0 = d0 + D
    }

    val digest = new Array[Byte](16)
    digest(0) = ((a0 >>> 24) & 0xff).toByte
    digest(1) = ((a0 >>> 16) & 0xff).toByte
    digest(2) = ((a0 >>> 8) & 0xff).toByte
    digest(3) = ((a0 & 0xff)).toByte
    digest(4) = ((b0 >>> 24) & 0xff).toByte
    digest(5) = ((b0 >>> 16) & 0xff).toByte
    digest(6) = ((b0 >>> 8) & 0xff).toByte
    digest(7) = ((b0 & 0xff)).toByte
    digest(8) = ((c0 >>> 24) & 0xff).toByte
    digest(9) = ((c0 >>> 16) & 0xff).toByte
    digest(10) = ((c0 >>> 8) & 0xff).toByte
    digest(11) = ((c0 & 0xff)).toByte
    digest(12) = ((d0 >>> 24) & 0xff).toByte
    digest(13) = ((d0 >>> 16) & 0xff).toByte
    digest(14) = ((d0 >>> 8) & 0xff).toByte
    digest(15) = ((d0 & 0xff)).toByte
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

  // def url(name: String): String = {
  //   s"data:image/svg+xml;utf8,${svg(name)}"
  // }

}
