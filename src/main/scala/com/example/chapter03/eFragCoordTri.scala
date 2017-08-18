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

package com.example.chapter03

import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.opengl._, GL11._, GL20._, GL30._
import GL45.glCreateVertexArrays

import com.example.common._

class eFragCoordTri extends ApplicationListener {
  import eFragCoordTriShaders._

  private val clearC = bufferF(Array(0.0f, 0.2f, 0.0f, 1.0f))
  private val vaos = bufferI(new Array(1))

  private lazy val program =
    loadProgram(vsSrc, fsSrc)

  override def init() {
    glCreateVertexArrays(vaos)
    glBindVertexArray(vaos get 0)

    // if it's the only program being used...
    glUseProgram(program)
  }

  override def render() {
    glClearBufferfv(GL_COLOR, 0, clearC)
    glDrawArrays(GL_TRIANGLES, 0, 3)
  }

  override def dispose() {
    glDeleteVertexArrays(vaos)
    glDeleteProgram(program)

    memFree(clearC)
    memFree(vaos)
  }
}

object eFragCoordTri extends App {
  val app = new DefaultLwjglApp(new eFragCoordTri()) {
    override val title  = "OpenGL SuperBible - Single Triangle"
    override val width  = 800
    override val height = 600
  }

  app.run()
}

object eFragCoordTriShaders {
  val vsSrc: (Int, String) =
    GL_VERTEX_SHADER ->
      """#version 410 core
        |
        |void main() {
        |  const vec4 vertices[3] = vec4[3](vec4( 0.25, -0.25, 0.5, 1.0),
        |                                   vec4(-0.25, -0.25, 0.5, 1.0),
        |                                   vec4( 0.25,  0.25, 0.5, 1.0));
        |
        |  gl_Position = vertices[gl_VertexID];
        |}""".stripMargin

  val fsSrc: (Int, String) =
    GL_FRAGMENT_SHADER ->
      """#version 410 core
        |
        |out vec4 color;
        |
        |void main() {
        |  color = vec4(sin(gl_FragCoord.x * 0.25) * 0.5 + 0.5,
        |               cos(gl_FragCoord.y * 0.25) * 0.5 + 0.5,
        |               sin(gl_FragCoord.x * 0.15) * cos(gl_FragCoord.y + 0.15),
        |               1.0);
        |}""".stripMargin
}
