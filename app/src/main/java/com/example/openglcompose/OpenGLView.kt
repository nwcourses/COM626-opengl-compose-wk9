package com.example.openglcompose

import android.opengl.GLSurfaceView
import android.content.Context
import android.graphics.SurfaceTexture
import javax.microedition.khronos.egl.EGLConfig
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import freemap.openglwrapper.GLMatrix
import javax.microedition.khronos.opengles.GL10
import freemap.openglwrapper.GPUInterface
import freemap.openglwrapper.OpenGLUtils
import java.io.IOException
import java.nio.FloatBuffer
import java.nio.ShortBuffer

// Note the onTextureAvailableCallback parameter
// You should call this, passing in the SurfaceTexture, once the SurfaceTexture has been created
// This will be forwarded onto the camera feed handling code, so that the camera feed can be streamed
// to the SurfaceTexture.
class OpenGLView(ctx: Context, val onTextureAvailableCallback: (SurfaceTexture) -> Unit) : GLSurfaceView(ctx), GLSurfaceView.Renderer {
    // Make the renderer an attribute of the OpenGLView so we can access
    // it from outside the OpenGLView

    init {
        setEGLContextClientVersion(2)
        setRenderer(this)
    }

    var fbuf: FloatBuffer? = null
    val gpu = GPUInterface("default")
    var sbuf: ShortBuffer? = null


    val red = floatArrayOf(1f, 0f, 0f, 1f)
    val yellow = floatArrayOf(1f, 1f, 0f, 1f)
    val blue = floatArrayOf(0f, 0f, 1f, 1f)

    // Create a variable to hold the view matrix
    var viewMatrix = GLMatrix()

    // Create a variable to hold the projection matrix
    val projectionMatrix = GLMatrix()

    var textureId = -1

    var surfaceTexture: SurfaceTexture? = null

    var orientationMatrix = GLMatrix()




    // We initialise the OpenGL view here
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        try {
            // Set the background colour (red=0, green=0, blue=0, alpha=1)
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

            // Enable depth testing - will cause nearer 3D objects to automatically
            // be drawn over further objects
            GLES20.glClearDepthf(1.0f)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)

            // Compile and link the shaders

            val success = gpu.loadShaders(context.assets, "vertex.glsl", "fragment.glsl")
            // Define the vertices we want to draw, and make a buffer from them

            val vertices = floatArrayOf(
                0f, 0f, -3f,
                1f, 0f, -3f,
                0.5f, 1f, -3f,
                -0.5f, 0f, -6f,
                0.5f, 0f, -6f,
                0f, 1f, -6f
            )

            fbuf = OpenGLUtils.makeFloatBuffer(vertices)

            sbuf = OpenGLUtils.makeShortBuffer(shortArrayOf(0, 1, 2, 2, 3, 0))

            // TODO setup your textures

        } catch (e: IOException) {
            Log.e("opengl-compose", e.stackTraceToString())
        }
        Log.d("opengl-compose", "onSurfaceCreated() complete")
    }

    // We draw our shapes here
    override fun onDrawFrame(unused: GL10) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Check we have a valid shader program and buffer
        fbuf?.apply {


            // TODO draw your camera feed texture

            // Reset the view matrix to the identity matrix
            //  viewMatrix.setAsIdentityMatrix()
            viewMatrix = orientationMatrix.clone()
            // TODO correct sensor matrix


            gpu.select()

            val refAttrib = gpu.getAttribLocation("aVertex")
            val refUColour = gpu.getUniformLocation("uColour")

            val refUView = gpu.getUniformLocation("uViewMtx")
            val refUProj = gpu.getUniformLocation("uProjMtx")



            gpu.sendMatrix(refUView, viewMatrix)
            gpu.sendMatrix(refUProj, projectionMatrix)


            gpu.setUniform4FloatArray(refUColour, blue)
            gpu.specifyBufferedDataFormat(refAttrib, this, 0)
            gpu.drawBufferedTriangles(0, 3)
            gpu.setUniform4FloatArray(refUColour, yellow)
            gpu.drawBufferedTriangles(3, 3)
        }
    }

    // Used if the screen is resized
    override fun onSurfaceChanged(unused: GL10, w: Int, h: Int) {
        GLES20.glViewport(0, 0, w, h)
        val hfov = 60.0f
        val aspect: Float = w.toFloat() / h
        projectionMatrix.setProjectionMatrix(hfov, aspect, 0.001f, 100f )
    }
}