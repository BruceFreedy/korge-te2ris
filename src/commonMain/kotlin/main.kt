import com.soywiz.klock.PerformanceCounter
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.component.onStageResized
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.addUpdater
import com.soywiz.korim.color.Colors

suspend fun main() = Korge(
	width = 400,
	height = 800,
	bgcolor = Colors["#000000"],
	title = "Te2ris",
	iconPath = "icon.png"
) {

	val grid = Grid(this)
	onStageResized { _, _ ->
		grid.sizeGrid()
	}

	var time_stamp = PerformanceCounter.milliseconds
	fun getPeriod() = PerformanceCounter.milliseconds - time_stamp

	addUpdater {
		grid.apply {
			if (pressed(Key.A, Key.LEFT)) {
				currentBlock.point.x -= 1
				if (isOverLap(currentBlock)) {
					currentBlock.point.x += 1
				}
				draw()
				return@addUpdater
			} else if (pressed(Key.D, Key.RIGHT)) {
				currentBlock.point.x += 1
				if (isOverLap(currentBlock)) {
					currentBlock.point.x -= 1
				}
				draw()
				return@addUpdater
			} else if (pressed(Key.W, Key.UP)) {
				currentBlock.dir += 1
				if (isOverLap(currentBlock)) {
					currentBlock.dir -= 1
				}
				draw()
				return@addUpdater
			} else if (pressed(Key.S, Key.DOWN)) {
				downToFinal()
				return@addUpdater
			} else if (pressing(Key.R, Key.LEFT_CONTROL)) {
				grid.blocks = ArrayList()
				grid.currentBlock = newRandomBlock()
				draw()
			}
		}
		if (getPeriod() < 300) return@addUpdater
		time_stamp = PerformanceCounter.milliseconds
		grid.toDown()
	}

}

fun Stage.pressed(vararg key: Key) = key.any { input.keys.justPressed(it) }
fun Stage.pressing(vararg key: Key) = key.all { input.keys.pressing(it) }


