package core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;

/* Created on February 16, 2024
@author David Eichinger
    Anticipating that the applications created will eventually feature user interaction and animation, this class will
    be designed to handle the standard phases or “life cycle” of such an application:
        • Startup: During this stage, objects are created, values are initialized, and any required external files are
                   loaded.
        • The Main Loop: This stage repeats continuously (typically 60 times per second) while the application is
                         running, and consists of the following three substages:
                            • Process Input: Check if the user has performed any action that sends data to the
                                                computer, such as pressing keys on a keyboard or clicking buttons on a
                                                mouse.
                            • Update: Changing values of variables and objects.
                            • Render: Create graphics that are displayed on the screen.
        • Shutdown: This stage typically begins when the user performs an action indicating that the program should stop
                    running (for example, by clicking a button to quit the application). This stage may involve tasks
                    such as signaling the application to stop checking for user input and closing any windows that were
                    created by the application.
 */
public abstract class Base {

    //window dimensions
    private int windowWidth;
    private int windowHeight;

    //the window handle
    private long window;

    //is the loop currently active?
    private boolean running;


    public Base() { }

    public void startup() {
        //intialize GLFW
        boolean initSuccess = glfwInit();
        if (!initSuccess)
            throw new RuntimeException("Unable to initalize GLFW");

        //create window and assoicated OpenGL context, which stores framebuffer and other state information
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        window = glfwCreateWindow( windowWidth, windowHeight,"Graphics Window", 0, 0);
        if ( window == 0 )
            throw new RuntimeException("Failed to create the GLFW window");

        running = true;

        //Make all OpenGL function calls apply to this context instance
        glfwMakeContextCurrent(window);

        //Specify number of screen updates to wait before swapping buffers.
        //Setting to 1 synchronizes the application frame rate with the display refresh rate; prevents visual
        // "screen tearing" artifacts
        glfwSwapInterval(1);

        //Detect current context and makes OpenGL bindings available for use:
        GL.createCapabilities();
    }


    public abstract void initialize();
    public abstract void update();

    public void run()
    {
        run(512, 512);
    }

    public void run(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        startup();

        // application-specific startup code
        initialize();

        //main loop
        while (running) {
            //check for user interaction events
            glfwPollEvents();

            // check if window close icon is clicked
            if (glfwWindowShouldClose(window))
                running = false;

            //application-specific update code
            update();

            //swap the color buffers to display rendered graphics on screen
            glfwSwapBuffers(window);
        }

        shutdown();;
    }
    public void shutdown() {
        //stop window monitoring for user input
        glfwFreeCallbacks(window);

        //close the window
        glfwDestroyWindow(window);

        //stop GLFW
        glfwTerminate();

        //stop error callback
        glfwSetErrorCallback(null).free();

    }
}