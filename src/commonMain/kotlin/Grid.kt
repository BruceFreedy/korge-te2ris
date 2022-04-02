import com.soywiz.kds.Queue
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.line
import com.soywiz.korge.view.position
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.concurrent.atomic.KorAtomicBoolean
import com.soywiz.korio.lang.currentThreadId
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointInt
import com.soywiz.korma.geom.PointIntArrayList

class Grid(
    private val stage: Stage,
    var blocks: ArrayList<Block> = ArrayList(),
    var queue: Queue<BlockDef> = Queue(),
) {

    var currentBlock: Block
    //draw board
    val xAmount = 10
    val yAmount = 20

    var pointX = 0.0
    var pointY = 0.0
    var width = 0.0
    var height = 0.0
    var xSize = 0.0
    var ySize = 0.0
    init {
        sizeGrid()
        drawGrid()
        currentBlock = newRandomBlock()
    }

    fun sizeGrid() {
        pointX = stage.width / 4
        pointY = stage.height / 4
        width = pointX * 3
        height = pointY * 3
        xSize = (stage.width / 2) / xAmount
        ySize = (stage.height / 2) / yAmount
    }

    fun drawGrid() {
        for (y in 0..yAmount) {
            for (x in 0..xAmount)
                stage.line(Point(pointX + x * xSize, pointY), Point(pointX + x * xSize, height))
            stage.line(Point(pointX, pointY + y * ySize), Point(width, pointY + y * ySize))
        }
    }


    fun draw(point: PointInt, color: RGBA) {
        stage.solidRect(xSize, ySize, color).position(
            pointX + point.x * xSize,
            pointY + point.y * ySize
        )
    }

    fun toDown() {
        currentBlock.point.y += 1
        if (isOverLap(currentBlock)) {
            currentBlock.point.y -= 1
            currentBlock = newRandomBlock(this, randomBlockDef(queue))
        }
        stage.draw()
    }

    fun downToFinal() {
            while (!isOverLap(currentBlock)) currentBlock.point.y += 1
            currentBlock.point.y -= 1
            currentBlock = newRandomBlock(this, randomBlockDef(queue))
            stage.draw()
    }

    fun Stage.draw() {
        solidRect(width, height, Colors["#000000"]).position(0, 0)
        drawGrid()
        blocks.forEach { block -> block.eachTile { draw(it, block.color) } }
    }

    fun isOverLap(block: Block): Boolean {
        val buffers = PointIntArrayList()
        blocks.forEach { if(it !== block) it.eachTile(buffers::add) }
        val b = KorAtomicBoolean(false)
        block.eachTile {
            if (it.y >= yAmount || it.x < 0 || it.x >= xAmount) {
                b.value = true; return@eachTile
            }
            buffers.fastForEach { x, y ->
                if (it.x == x && it.y == y) {
                    b.value = true; return@eachTile
                }
            }
        }
        return b.value
    }

    fun randomBlockDef(queue: Queue<BlockDef>): BlockDef {
        if (queue.isEmpty()) {
            BlockDef.values().apply {
                shuffle()
                forEach { queue.enqueue(it) }
            }
        }
        val res = queue.peek()!!
        queue.dequeue()
        return res
    }

    fun newRandomBlock(grid: Grid = this, blockDef: BlockDef = randomBlockDef(queue)): Block {
        return Block(blockDef, PointInt(grid.xAmount/2, 0)).apply { blocks.add(this); }
    }



}

class Block(
    val block: BlockDef,
    val point: PointInt,
) {
    val color: RGBA = block.color
    var dir = 0
        set(value) {
            field = value%4
        }
    val tiles get() = block.dirTiles[dir]

    fun eachTile(consumer: (PointInt) -> Unit) {
        tiles.fastForEach { x, y ->
            consumer.invoke(PointInt(point.x + x, point.y + y))
        }
    }
/*
    fun spin(dir: Int = -1) {
        val tiles = PointIntArrayList()
        this.tiles.fastForEach { x, y -> tiles.add(y * dir, x * -dir) }
        this.tiles = tiles
    }
*/
}
