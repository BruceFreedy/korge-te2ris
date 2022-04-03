import com.soywiz.kds.Queue
import com.soywiz.kds.iterators.fastForEach
import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.concurrent.atomic.KorAtomicBoolean
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointInt
import com.soywiz.korma.geom.PointIntArrayList

class Grid(
    private val stage: Stage,
    var blocks: ArrayList<Block> = ArrayList(),
    var queue: Queue<BlockDef> = Queue(),
) {
    var props = ArrayList<View>()

    fun View.addProps() {
        this@Grid.props.add(this)
    }
    fun clearProps() {
        props.fastForEach { it.removeFromParent() }
        props = ArrayList()
    }

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
        width = (stage.width/2 + pointX)
        height = (stage.height/2 + pointY)
        xSize = (width - pointX) / xAmount
        ySize = (height - pointY) / yAmount
    }

    fun drawGrid() {
        for (y in 0..yAmount) {
            for (x in 0..xAmount)
                stage.line(Point(pointX + x * xSize, pointY), Point(pointX + x * xSize, height)).addProps()
            stage.line(Point(pointX, pointY + y * ySize), Point(width, pointY + y * ySize)).addProps()
        }
    }


    fun draw(point: PointInt, color: RGBA) {
        stage.solidRect(xSize, ySize, color).position(
            pointX + point.x * xSize,
            pointY + point.y * ySize
        ).addProps()
    }

    fun toDown() {
        currentBlock.point.y += 1
        if (isOverLap(currentBlock)) {
            currentBlock.point.y -= 1
            currentBlock = newRandomBlock(this, randomBlockDef(queue))
        }
        strikeLine()
        draw()
    }

    fun downToFinal() {
        while (!isOverLap(currentBlock)) currentBlock.point.y += 1
        currentBlock.point.y -= 1
        currentBlock = newRandomBlock(this, randomBlockDef(queue))
        strikeLine()
        draw()
    }

    fun draw() {
        clearProps()
        blocks.forEach { block -> block.eachTile { draw(it, block.color) } }
        drawGrid()
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

    fun resetMap() {
        blocks = ArrayList()
    }

    fun newRandomBlock(grid: Grid = this, blockDef: BlockDef = randomBlockDef(queue)) =
        Block(blockDef, PointInt(grid.xAmount/2, 0)).apply {
            if(isOverLap(this)) {
                resetMap()
            }
            blocks.add(this)
        }

    fun strikeLine() {
        val map = HashMap<Int, Int>()
        blocks.forEach { block -> block.eachTile { map[it.y] = (map[it.y]?: 0) + 1 } }
        val striked = map.filter { entry -> entry.value >= xAmount }.toList()
        blocks.forEach { block ->
            block.replaceTileIf { origin, point ->
                if (striked.any { it.first == point.y }) null
                else origin.apply {
                    y += striked.count { it.first >= point.y }
                }
            }
        }
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
    val dirTiles = ArrayList<PointIntArrayList>().apply {  //clone PointIntArrayList
        block.dirTiles.fastForEach { add(PointIntArrayList().apply { it.fastForEach { x, y -> add(x, y) } }) }
    }
    val tiles get() = dirTiles[dir]

    fun eachTile(consumer: (PointInt) -> Unit) {
        tiles.fastForEach { x, y ->
            consumer.invoke(PointInt(point.x + x, point.y + y))
        }
    }


    fun replaceTileIf(function: (origin: PointInt, point: PointInt) -> PointInt?) {
        val newTiles = PointIntArrayList()
        tiles.fastForEach { x, y ->
            function.invoke(PointInt(x, y), PointInt(point.x + x, point.y + y))?.let { newTiles.add(it) }
        }
        tiles.clear()
        newTiles.fastForEach { x, y -> tiles.add(x, y) }
    }

}
