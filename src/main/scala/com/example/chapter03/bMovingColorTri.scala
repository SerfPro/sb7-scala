/*******************************************************************************
 * Copyright 2015 Serf Productions, LLC.
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

package com.example.chapter03

import scala.math.{sin, cos}

import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.opengl._, GL11._, GL20._, GL30._
import GL45.glCreateVertexArrays

import com.example.common._

class bMovingColorTri extends ApplicationListener {
  import bMovingColorTriShaders._

  private val clearC = bufferF(Array(0.0f, 0.2f, 0.0f, 1.0f))
  private val triC   = bufferF(Array(0.0f, 0.0f, 0.0f, 1.0f))
  private val offset = bufferF(Array(0.0f, 0.0f, 0.0f, 0.0f))

  private val vaos = bufferI(new Array(1))

  private lazy val program = loadProgram(vsSrc, fsSrc)

  override def init() {
    glCreateVertexArrays(vaos)
    glBindVertexArray(vaos get 0)

    glUseProgram(program)
  }

  override def update(time: Double) {
    triC.put(0, sin(time).toFloat*0.5f+0.5f)
    triC.put(1, cos(time).toFloat*0.5f+0.5f)

    offset.put(0, sin(time).toFloat*0.5f)
    offset.put(1, cos(time).toFloat*0.6f)
  }

  override def render() {
    glClearBufferfv(GL_COLOR, 0, clearC)

    glVertexAttrib4fv(0, offset)
    glVertexAttrib4fv(1, triC)
    glDrawArrays(GL_TRIANGLES, 0, 3)
  }

  override def dispose() {
    glDeleteVertexArrays(vaos)
    glDeleteProgram(program)

    memFree(clearC)
    memFree(triC)
    memFree(offset)
    memFree(vaos)
  }
}

object bMovingColorTri extends App {
  val app = new DefaultLwjglApp(new bMovingColorTri()) {
    override val title  = "OpenGL SuperBible - Moving Triangle"
    override val width  = 800
    override val height = 600
  }

  app.run()
}

object bMovingColorTriShaders {
  val vsSrc: (Int, String) =
    GL_VERTEX_SHADER ->
      """#version 410 core
        |
        |layout (location = 0) in vec4 offset;
        |layout (location = 1) in vec4 color;
        |
        |out VS_OUT {
        |  vec4 color;
        |} vs_out;
        |
        |void main(void) {
        |  const vec4 vertices[3] = vec4[3](vec4( 0.25, -0.25, 0.5, 1.0),
        |                                   vec4(-0.25, -0.25, 0.5, 1.0),
        |                                   vec4( 0.25,  0.25, 0.5, 1.0));
        |
        |  gl_Position = vertices[gl_VertexID] + offset;
        |  vs_out.color = color;
        |}""".stripMargin

  val fsSrc: (Int, String) =
    GL_FRAGMENT_SHADER ->
      """#version 410 core
        |
        |in VS_OUT {
        |  vec4 color;
        |} fs_in;
        |
        |out vec4 color;
        |
        |void main(void) {
        |  color = fs_in.color;
        |}""".stripMargin
}
