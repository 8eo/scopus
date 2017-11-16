/*
 * Copyright © ${year} 8eo Inc.
 */
package za.co.monadic.scopus.g711μ

import za.co.monadic.scopus.{Encoder, SampleFrequency}

import scala.util.{Success, Try}

case class G711μEncoder(sampleFreq: SampleFrequency, channels: Int) extends Encoder {

  private val BIAS     = 0x84 /* Bias for linear code. */
  private val CLIP     = 8159
  private val PCM_NORM = 32124.0f /* Normalization factor for Float to PCM */
  private val uEnd     = Array[Int](0x3F, 0x7F, 0xFF, 0x1FF, 0x3FF, 0x7FF, 0xFFF, 0x1FFF)

  private def searchUEnd(x: Int): Int = {
    var i = 0
    while ((i < uEnd.length) && (x > uEnd(i))) {
      i += 1
    }
    i
  }

  private def toMu(xIn: Short): Byte = {
    val xScale: Int = xIn >> 2
    var (x, mask) = if (xScale < 0) {
      (-xScale - 1, 0x7F)
    } else {
      (xScale, 0xFF)
    }
    if (x > CLIP) x = CLIP /* clip the magnitude */
    x += (BIAS >> 2)

    /* Convert the scaled magnitude to segment number. */
    val seg = searchUEnd(x)

    /*
     * Combine the sign, segment, quantization bits;
     * and complement the code word.
     */
    if (seg >= 8)
      ((0x7F ^ mask) & 0xFF).toByte /* out of range, return maximum value. */
    else {
      val uval = (seg << 4) | ((x >> (seg + 1)) & 0xF)
      ((uval ^ mask) & 0xFF).toByte
    }
  }

  private def toMu(xIn: Float): Byte = toMu((xIn * PCM_NORM).toShort)

  /**
    * Encode a block of raw audio  in integer format using the configured encoder
    *
    * @param audio Audio data arranged as a contiguous block interleaved array of short integers
    * @return An array containing the compressed audio or the exception in case of a failure
    */
  override def apply(audio: Array[Short]): Try[Array[Byte]] = {
    val out = new Array[Byte](audio.length)
    var i   = 0
    while (i < audio.length) {
      out(i) = toMu(audio(i))
      i += 1
    }
    Success(out)
  }

  /**
    * Encode a block of raw audio  in float format using the configured encoder
    *
    * @param audio Audio data arranged as a contiguous block interleaved array of floats
    * @return An array containing the compressed audio or the exception in case of a failure
    */
  override def apply(audio: Array[Float]): Try[Array[Byte]] = {
    val out = new Array[Byte](audio.length)
    var i   = 0
    while (i < audio.length) {
      out(i) = toMu(audio(i))
      i += 1
    }
    Success(out)
  }

  /**
    * Set the complexity of the encoder. This has no effect if the encoder does not support
    * complexity settings
    *
    * @param c A value between 0 and 10 indicating the encoder complexity.
    * @return A reference to the updated encoder
    */
  override def complexity(c: Int): Encoder = this

  /**
    * Release all pointers allocated for the encoder. Make every attempt to call this
    * when you are done with the encoder as finalise() is what it is in the JVM
    */
  override def cleanup(): Unit = ()

  /**
    * @return A discription of this instance of an encoder or decoder
    */
  override def getDetail: String = "G.711u μ-law encoder"

  /**
    * Reset the underlying codec.
    */
  override def reset: Int = 0

  /**
    * @return The sample rate for this codec's instance
    */
  override def getSampleRate: Int = sampleFreq()
}
