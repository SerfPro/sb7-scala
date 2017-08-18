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
import org.lwjgl.opengl._, GL11._, GL20._, GL30._, GL40._
import GL45.glCreateVertexArrays

import com.example.common._

class cTessellatedTri extends ApplicationListener {
  import cTessellatedTriShaders._

  private val clearC = bufferF(Array(0.0f, 0.2f, 0.0f, 1.0f))
  private val vaos   = bufferI(new Array(1))

  private lazy val program =
    loadProgram (
      GL_VERTEX_SHADER          -> vsSrc,
      GL_TESS_CONTROL_SHADER    -> tcsSrc,
      GL_TESS_EVALUATION_SHADER -> tesSrc,
      GL_FRAGMENT_SHADER        -> fsSrc
    )

  override def init() {
    glCreateVertexArrays(vaos)
    glBindVertexArray(vaos get 0)
    glUseProgram(program)

    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
  }

  override def render() {
    glClearBufferfv(GL_COLOR, 0, clearC)
    glDrawArrays(GL_PATCHES, 0, 3)
  }

  override def dispose() {
    glDeleteVertexArrays(vaos)
    glDeleteProgram(program)

    memFree(clearC)
    memFree(vaos)
  }
}

object cTessellatedTri extends App {
  val app = new DefaultLwjglApp(new cTessellatedTri()) {
    override val title  = "OpenGL SuperBible - Tessellated Triangle"
    override val width  = 800
    override val height = 600
  }

  app.run()
}

object cTessellatedTriShaders {
  val vsSrc: String = """
    |#version 410 core
    |
    |void main(void) {
    |  const vec4 vertices[] = vec4[](vec4( 0.25, -0.25, 0.5, 1.0),
    |                                 vec4(-0.25, -0.25, 0.5, 1.0),
    |                                 vec4( 0.25,  0.25, 0.5, 1.0));
    |
    |  gl_Position = vertices[gl_VertexID];
    |}""".stripMargin

  val tcsSrc: String = """
    |#version 410 core
    |
    |layout (vertices = 3) out;
    |
    |void main(void) {
    |  if (gl_InvocationID == 0) {
    |    gl_TessLevelInner[0] = 5.0;
    |    gl_TessLevelOuter[0] = 5.0;
    |    gl_TessLevelOuter[1] = 5.0;
    |    gl_TessLevelOuter[2] = 5.0;
    |  }
    |
    |  gl_out[gl_InvocationID].gl_Position =
    |    gl_in[gl_InvocationID].gl_Position;
    |}""".stripMargin

  val tesSrc: String = """
    |#version 410 core
    |
    |layout (triangles, equal_spacing, cw) in;
    |
    |void main(void) {
    |  gl_Position = (gl_TessCoord.x * gl_in[0].gl_Position) +
    |                (gl_TessCoord.y * gl_in[1].gl_Position) +
    |                (gl_TessCoord.z * gl_in[2].gl_Position);
    |}""".stripMargin

  val fsSrc: String = """
    |#version 410 core
    |
    |out vec4 color;
    |
    |void main(void) {
    |  color = vec4(0.0, 0.8, 1.0, 1.0);
    |}""".stripMargin
}
