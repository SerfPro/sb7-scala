/*******************************************************************************
 * Copyright 2015 Serf Productions, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.example

import java.nio.{FloatBuffer, IntBuffer}
import java.io.Closeable
import scala.util.control.NonFatal
import scala.util.{Failure, Try}

import org.lwjgl.system.MemoryUtil

package object common {

  /**
   * Mimics Java's "try-with-resources". Should be useful for providing the
   * same semantic use of MemoryStack as shown in the LWJGL Blog:
   * http://blog.lwjgl.org/memory-management-in-lwjgl-3/
   */
  def tryWith[C <: Closeable, R](resource: => C)(f: C => R): Try[R] =
    Try(resource) flatMap { ri =>
      try {
        val retVal = f(ri)
        Try(ri.close()) map { _ => retVal }
      } catch { case NonFatal(funcEx) =>
        try {
          ri.close()
          Failure(funcEx)
        } catch { case NonFatal(closeEx) =>
          funcEx addSuppressed closeEx
          Failure(funcEx)
        }
      }
    }

  def bufferF(floats: Array[Float]): FloatBuffer = {
    val fb = MemoryUtil.memAllocFloat(floats.length)
    fb.put(floats)
    fb.flip()
    fb
  }

  def bufferI(ints: Array[Int]): IntBuffer = {
    val ib = MemoryUtil.memAllocInt(ints.length)
    ib.put(ints)
    ib.flip()
    ib
  }

  import org.lwjgl.opengl._, GL11._, GL20._, GL40._

  /**
   * Loads a Shader Program from Shader sources.
   *
   * Useful for 1 Shader Program per example... A real application should
   * probably use a more robust "Shader/Program Manager" component.
   */
  def loadProgram(sources: (Int, String)*): Int = {
    val shaders = sources map { case (typ, source) =>
      loadShader(typ, source)
    }

    val program = glCreateProgram()
    shaders.foreach(shader => glAttachShader(program, shader))
    glLinkProgram(program)

    if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
      throw new RuntimeException(glGetProgramInfoLog(program))

    shaders.foreach(glDeleteShader)

    program
  }

  /**
   * Compiles a shader from source and returns the shader's "name".
   *
   * Useful for our examples... A real application should compile multiple
   * shaders, then check on whether or not they succeeded.
   */
  def loadShader(typ: Int, source: String): Int = {
    val shader = glCreateShader(typ)
    glShaderSource(shader, source)
    glCompileShader(shader)

    if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
      throw new RuntimeException(s"Error compiling ${shaderName(typ)} shader")

    shader
  }

  /**
   * Returns a human-readable shader-type name for the given shader type.
   */
  private def shaderName(typ: Int): String =
    typ match {
      case GL_VERTEX_SHADER          => "Vertex"
      case GL_FRAGMENT_SHADER        => "Fragment"
      case GL_TESS_CONTROL_SHADER    => "Tessellation Control"
      case GL_TESS_EVALUATION_SHADER => "Tessellation Evaluation"
      case _ => "unknown"
    }
}