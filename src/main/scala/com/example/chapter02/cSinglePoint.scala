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

package com.example.chapter02

import scala.math.{sin, cos}

import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.opengl._, GL11._, GL20._, GL30._
import GL45.glCreateVertexArrays

import com.example.common._

class SinglePoint extends ApplicationListener {
  import SinglePointShaders._

  private val clearC = bufferF(Array(0.0f, 0.2f, 0.0f, 1.0f))
  private val vaos = bufferI(new Array(1))

  private var program: Int = _
  //private var vao:     Int = _

  override def init() {
    initProgram()
    glPointSize(40.0f)

    //// VAO is required despite no input in this example ////

    // the older method of creating a VAO
    //vao = glGenVertexArrays()
    //glBindVertexArray(vao)

    // the new 4.5 way to create a VAO
    glCreateVertexArrays(vaos)
    glBindVertexArray(vaos.get(0))
  }

  override def update(time: Double) {
    clearC.put(0, sin(time).toFloat*0.5f+0.5f)
    clearC.put(1, cos(time).toFloat*0.5f+0.5f)
  }

  override def render() {
    glClearBufferfv(GL_COLOR, 0, clearC)
    glUseProgram(program)
    glDrawArrays(GL_POINTS, 0, 1)
  }

  override def dispose() {
    glDeleteVertexArrays(vaos)
    glDeleteProgram(program)

    memFree(clearC)
    memFree(vaos)
  }

  // subsequent examples will use a simple utility for this
  private def initProgram() {
    val vertexShader = glCreateShader(GL_VERTEX_SHADER)
    glShaderSource(vertexShader, vertexShaderSrc)
    glCompileShader(vertexShader)

    if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE)
      throw new RuntimeException("Error compiling vertex shader.")

    val fragShader = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(fragShader, fragShaderSrc)
    glCompileShader(fragShader)

    if (glGetShaderi(fragShader, GL_COMPILE_STATUS) == GL_FALSE)
      throw new RuntimeException("Error compiling fragment shader.")

    program = glCreateProgram()
    glAttachShader(program, vertexShader)
    glAttachShader(program, fragShader)
    glLinkProgram(program)

    if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
      throw new RuntimeException(glGetProgramInfoLog(program))

    glDeleteShader(vertexShader)
    glDeleteShader(fragShader)
  }
}

object SinglePoint extends App {
  val app = new DefaultLwjglApp(new SinglePoint()) {
    override val title  = "OpenGL SuperBible - Single Point"
    override val width  = 800
    override val height = 600
  }

  app.run()
}

object SinglePointShaders {
  val vertexShaderSrc: String =
    """#version 410 core
      |
      |void main() {
      |  gl_Position = vec4(0.0, 0.0, 0.0, 1.0);
      |}""".stripMargin

  val fragShaderSrc: String =
    """#version 410 core
      |
      |out vec4 color;
      |
      |void main() {
      |  color = vec4(0.0, 0.8, 1.0, 1.0);
      |}""".stripMargin
}
