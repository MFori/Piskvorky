package view

import core.component.CoreComponent
import core.component.CoreRProps
import core.component.coreChild
import cz.martinforejt.piskvorky.api.model.BoardValue
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.id
import model.GameVO
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get
import react.RBuilder
import react.RState
import react.dom.canvas
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor

/**
 * Created by Martin Forejt on 28.12.2020.
 * me@martinforejt.cz
 *
 * @author Martin Forejt
 */

class GameBoardProps : CoreRProps() {
    var game: GameVO? = null
    var onMove: ((Int, Int) -> Unit)? = null
}

class GameBoard : CoreComponent<GameBoardProps, RState>() {

    private var ctx: CanvasRenderingContext2D? = null
    private var width = 0.0
    private var height = 0.0

    private var moved = false
    private var isDragging = false
    private var clickX = 0
    private var clickY = 0
    private var dragX = 0
    private var dragY = 0
    private var offsetX = 0
    private var offsetY = 0

    var mouseX: Int? = null
    var mouseY: Int? = null

    override fun RBuilder.render() {
        canvas {
            attrs {
                id = "canvas"
                width = "${window.innerWidth}px"
                height = "${window.innerHeight}px"
            }
        }
    }

    override fun componentDidUpdate(prevProps: GameBoardProps, prevState: RState, snapshot: Any) {
        val c = document.getElementById("canvas") as HTMLCanvasElement
        ctx = c.getContext("2d") as CanvasRenderingContext2D
        width = ctx!!.canvas.width.toDouble()
        height = ctx!!.canvas.height.toDouble()

        c.onmousedown = onMouseDown
        c.onmouseup = onMouseUp
        c.onmouseout = onMouseOut
        c.onmousemove = onMouseMove
        c.addEventListener("touchstart", onTouchStart, null)
        c.addEventListener("touchend", onTouchEnd, null)
        c.addEventListener("touchmove", onTouchMove, null)
        redraw(ctx!!, width, height)
    }

    private fun me() = if (props.game?.cross?.email == user!!.email) BoardValue.cross else BoardValue.nought

    private val onMouseDown: (MouseEvent) -> Unit = {
        isDragging = true
        dragX = it.clientX
        dragY = it.clientY
        clickX = it.clientX
        clickY = it.clientY
    }

    private val onTouchStart: (Event) -> Unit = {
        val event = it as TouchEvent
        val touch = event.touches[0]
        if (touch != null) {
            isDragging = true
            dragX = touch.clientX
            dragY = touch.clientY
            clickX = touch.clientX
            clickY = touch.clientY
        }
    }

    private val onMouseUp: (MouseEvent) -> Unit = {
        if(!moved && me() == props.game?.current) {
            if (isDragging && abs(clickX - it.clientX) < 20 && abs(clickY - it.clientY) < 20) {
                getMouse(it.clientX, it.clientY)
                if (props.game?.isEmpty(mouseX!!, mouseY!!) == true) {
                    props.game?.set(mouseX!!, mouseY!!, props.game?.current ?: BoardValue.none)
                    moved = true
                    props.onMove?.invoke(mouseX!!, mouseY!!)
                }
                redraw(ctx!!, width, height)
            }
        }
        isDragging = false
        mouseX = null
        mouseY = null
    }

    private val onTouchEnd: (Event) -> Unit = {
        isDragging = false
        mouseX = null
        mouseY = null
    }

    private val onMouseOut: (MouseEvent) -> Unit = {
        if (mouseX != null) {
            mouseX = null
            mouseY = null
            redraw(ctx!!, width, height)
        }
        isDragging = false
    }

    private val onTouchMove: (Event) -> Unit = {
        val event = it as TouchEvent
        val touch = event.touches[0]
        if (touch != null) {
            onMouseMove(touch.clientX, touch.clientY)
        }
    }

    private val onMouseMove: (MouseEvent) -> Unit = {
        val x = it.clientX
        val y = it.clientY
        onMouseMove(x, y)
    }

    private fun onMouseMove(x: Int, y: Int) {
        if (isDragging) {
            offsetX -= dragX - x
            offsetY -= dragY - y
        }

        dragX = x
        dragY = y

        getMouse(x, y)
        redraw(ctx!!, width, height)
    }

    private fun getMouse(x: Int, y: Int) {

        val mouseX = (-offsetX + x).toDouble() / CELL_SIZE
        val mouseY = (-offsetY + y).toDouble() / CELL_SIZE

        this.mouseX = floor(mouseX).toInt()
        this.mouseY = floor(mouseY).toInt()
    }

    private fun redraw(ctx: CanvasRenderingContext2D, width: Double, height: Double) {
        ctx.clear(width, height)

        val cellsX = width.toInt() / CELL_SIZE
        val offsetX = (offsetX % CELL_SIZE).toDouble()
        val cellsY = height.toInt() / CELL_SIZE
        val offsetY = (offsetY % CELL_SIZE).toDouble()

        ctx.drawBackground(width, height, cellsX, offsetX, cellsY, offsetY)
        ctx.drawMouse(offsetX, offsetY)
        ctx.drawMoves(cellsX, offsetX, cellsY, offsetY)
    }

    private fun CanvasRenderingContext2D.drawMoves(cellsX: Int, offsetX: Double, cellsY: Int, offsetY: Double) {
        for (x in -1..cellsX + 1) {
            for (y in -1..cellsY + 1) {
                val xx = x - (this@GameBoard.offsetX / CELL_SIZE)
                val yy = y - (this@GameBoard.offsetY / CELL_SIZE)

                strokeStyle = "#fff"
                lineWidth = 3.0
                //strokeText("$xx:$yy", offsetX + x * CELL_SIZE, offsetY + y * CELL_SIZE + CELL_SIZE / 2)

                if (props.game?.isEmpty(xx, yy) == false) {
                    val value = props.game!![xx, yy]
                    if(value != BoardValue.cross) {
                        beginPath()
                        moveTo(offsetX + x * CELL_SIZE + 15, offsetY + y * CELL_SIZE + 15)
                        lineTo(offsetX + x * CELL_SIZE + CELL_SIZE - 15, offsetY  + y * CELL_SIZE + CELL_SIZE - 15)
                        moveTo(offsetX + x * CELL_SIZE + CELL_SIZE - 15, offsetY + y * CELL_SIZE + 15)
                        lineTo(offsetX + x * CELL_SIZE + 15, offsetY  + y * CELL_SIZE + CELL_SIZE - 15)
                        stroke()
                    } else {
                        beginPath()
                        arc(offsetX + x * CELL_SIZE + CELL_SIZE/2, offsetY + y* CELL_SIZE + CELL_SIZE/2,
                            (CELL_SIZE / 2 - 15).toDouble(),0.0,2* PI)
                        stroke()
                    }
                }
            }
        }
    }

    private fun CanvasRenderingContext2D.drawMouse(offsetX: Double, offsetY: Double) {
        if (mouseX != null && mouseY != null) {
            strokeStyle = "#5d7191"
            fillStyle = "#5d7191"
            fillRect(
                offsetX + (mouseX!! + (this@GameBoard.offsetX / CELL_SIZE)) * CELL_SIZE,
                offsetY + (mouseY!! + (this@GameBoard.offsetY / CELL_SIZE)) * CELL_SIZE,
                CELL_SIZE.toDouble(),
                CELL_SIZE.toDouble()
            )
        }
    }

    private fun CanvasRenderingContext2D.drawBackground(
        width: Double,
        height: Double,
        cellsX: Int,
        offsetX: Double,
        cellsY: Int,
        offsetY: Double
    ) {
        lineWidth = 1.0
        strokeStyle = "#5d7191"

        beginPath()
        for (i in -1..cellsX + 1) {
            moveTo(offsetX + i * CELL_SIZE, 0.0)
            lineTo(offsetX + i * CELL_SIZE, height)
        }
        stroke()
        beginPath()
        for (i in -1..cellsY + 1) {
            moveTo(0.0, offsetY + i * CELL_SIZE)
            lineTo(width, offsetY + i * CELL_SIZE)
        }
        stroke()
    }

    private fun CanvasRenderingContext2D.clear(width: Double, height: Double) {
        clearRect(0.0, 0.0, width, height)
    }

    companion object {
        const val CELL_SIZE = 80
    }
}

fun RBuilder.gameBoard(parent: CoreComponent<*, *>, game: GameVO?, onMove: (Int, Int) -> Unit) =
    coreChild(parent, GameBoard::class) {
        attrs.game = game
        attrs.onMove = onMove
    }