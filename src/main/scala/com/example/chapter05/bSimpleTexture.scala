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

package com.example.chapter05

import java.nio.FloatBuffer

import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.{memAllocFloat, memFree}

import org.lwjgl.opengl._, GL11._, GL20._, GL30._
import GL45.glCreateVertexArrays
import ARBTextureStorage._

import com.example.common._

class bSimpleTexture extends ApplicationListener {
  import bSimpleTextureShaders._

  private val clr = Array(0.0f, 0.25f, 0.0f, 1.0f)

  private lazy val (vao, txt) =
    tryWith(stackPush) { stack =>
      val vaos = stack mallocInt 1
      glCreateVertexArrays(vaos)

      val txts = stack mallocInt 1
      glGenTextures(txts)

      (vaos get 0, txts get 0)
    }.get

  private lazy val program =
    loadProgram(vsSrc, fsSrc)

  override def init() {
    glBindTexture(GL_TEXTURE_2D, txt)
    glTexStorage2D(GL_TEXTURE_2D, 8, GL_RGBA32F, 256, 256)

    val texData = memAllocFloat(256*256*4)
    try {
      genTexture(texData, 256, 256)
      glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 256, 256, GL_RGBA, GL_FLOAT, texData)
    } finally {
      memFree(texData)
    }

    glBindVertexArray(vao)
  }

  override def render() {
    glClearBufferfv(GL_COLOR, 0, clr)

    glUseProgram(program)
    glDrawArrays(GL_TRIANGLES, 0, 3)
  }

  override def dispose() {
    glDeleteProgram(program)
    glDeleteVertexArrays(vao)
    glDeleteTextures(txt)
  }

  private def genTexture(data: FloatBuffer, width: Int, height: Int): Unit = {
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        data.put((y*width+x)*4+0, ((x&y)&0xFF)/255.0f)
        data.put((y*width+x)*4+1, ((x&y)&0xFF)/255.0f)
        data.put((y*width+x)*4+2, ((x&y)&0xFF)/255.0f)
        data.put((y*width+x)*4+3, 1.0f)
      }
    }
  }
}

object bSimpleTexture extends App {
  val app = new DefaultLwjglApp(new bSimpleTexture()) {
    override val title  = "OpenGL SuperBible - Simple Texture"
    override val width  = 800
    override val height = 600
  }

  app.run()
}

object bSimpleTextureShaders {
  val vsSrc: (Int, String) =
    GL_VERTEX_SHADER ->
      """#version 410 core
        |
        |void main() {
        |  const vec4 vertices[3] = vec4[3](vec4( 0.75, -0.75, 0.5, 1.0),
        |                                   vec4(-0.75, -0.75, 0.5, 1.0),
        |                                   vec4( 0.75,  0.75, 0.5, 1.0));
        |
        |  gl_Position = vertices[gl_VertexID];
        |}""".stripMargin

  val fsSrc: (Int, String) =
    GL_FRAGMENT_SHADER ->
      """#version 410 core
        |
        |uniform sampler2D s;
        |
        |out vec4 color;
        |
        |void main() {
        |  color = texture(s, gl_FragCoord.xy / textureSize(s,0));
        |  //color = texelFetch(s, ivec2(gl_FragCoord.xy), 0);
        |}""".stripMargin
}
