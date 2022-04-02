import com.soywiz.klock.PerformanceCounter
import com.soywiz.korev.Key
import com.soywiz.korge.Korge
import com.soywiz.korge.component.onStageResized
import com.soywiz.korge.input.keys
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


	root.keys.down {
		grid.apply {
			when (it.key) {
				Key.LEFT -> {
					currentBlock.point.x -= 1
					if (isOverLap(currentBlock)) {
						currentBlock.point.x += 1
					}
					draw()
				}
				Key.RIGHT -> {
					currentBlock.point.x += 1
					if (isOverLap(currentBlock)) {
						currentBlock.point.x -= 1
					}
					draw()
				}
				Key.SPACE, Key.UP -> {
					currentBlock.dir += 1
					if (isOverLap(currentBlock)) {
						currentBlock.dir -= 1
					}
					draw()
				}
				Key.DOWN -> {
					downToFinal()
				}
				else -> Unit
			}
		}	}


	var time_stamp = PerformanceCounter.milliseconds
	fun getPeriod() = PerformanceCounter.milliseconds - time_stamp

	addUpdater {
		if (getPeriod() < 300) return@addUpdater
		time_stamp = PerformanceCounter.milliseconds
		if (input.keys.pressing(Key.DOWN)) grid.toDown()
		grid.toDown()
	}


}



