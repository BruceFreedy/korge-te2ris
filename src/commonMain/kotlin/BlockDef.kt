import ColorUtil.hex
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.PointInt
import com.soywiz.korma.geom.PointIntArrayList

enum class BlockDef(val color: RGBA, vararg argTiles: Int) {
	_1("FFD124".hex(),
		0, 0, 0, 0,
		0, 0, 1, 0,
		0, 1, 1, 1,
		0, 0, 0, 0,

		0, 0, 0, 0,
		0, 0, 1, 0,
		0, 0, 1, 1,
		0, 0, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 1,
		0, 0, 1, 0,

		0, 0, 0, 0,
		0, 0, 1, 0,
		0, 1, 1, 0,
		0, 0, 1, 0,
	), _2("00AFC1".hex(),
		0, 0, 1, 0,
		0, 0, 1, 0,
		0, 0, 1, 0,
		0, 0, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		1, 1, 1, 1,
		0, 0, 0, 0,

		0, 0, 1, 0,
		0, 0, 1, 0,
		0, 0, 1, 0,
		0, 0, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		1, 1, 1, 1,
		0, 0, 0, 0,
	), _3("006778".hex(),
		0, 0, 0, 0,
		0, 1, 1, 0,
		0, 0, 1, 0,
		0, 0, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 1,
		0, 1, 1, 1,
		0, 0, 0, 0,

		0, 0, 0, 0,
		0, 0, 1, 0,
		0, 0, 1, 0,
		0, 0, 1, 1,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 1,
		0, 1, 0, 0,
	), _4("22577E".hex(),
		0, 0, 0, 0,
		0, 0, 1, 1,
		0, 0, 1, 0,
		0, 0, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 1,
		0, 0, 0, 1,

		0, 0, 0, 0,
		0, 0, 1, 0,
		0, 0, 1, 0,
		0, 1, 1, 0,

		0, 0, 0, 0,
		0, 1, 0, 0,
		0, 1, 1, 1,
		0, 0, 0, 0,
	), _5("5584AC".hex(),
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 0,
		0, 1, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 0,
		0, 1, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 0,
		0, 1, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 0,
		0, 1, 1, 0,
	), _6("95D1CC".hex(),
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 0,
		0, 0, 1, 1,

		0, 0, 0, 0,
		0, 0, 1, 0,
		0, 1, 1, 0,
		0, 1, 0, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 1, 1, 0,
		0, 0, 1, 1,

		0, 0, 0, 0,
		0, 0, 1, 0,
		0, 1, 1, 0,
		0, 1, 0, 0,
	), _7("FAFFAF".hex(),
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 1, 1,
		0, 1, 1, 0,

		0, 0, 0, 0,
		0, 1, 0, 0,
		0, 1, 1, 0,
		0, 0, 1, 0,

		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 1, 1,
		0, 1, 1, 0,

		0, 0, 0, 0,
		0, 1, 0, 0,
		0, 1, 1, 0,
		0, 0, 1, 0,
	),
	;
	val centerY = (argTiles.indexOf(1)/width.toDouble()).toInt()
	val dirTiles = tile(*argTiles)

	private val width get() = 4
	private val height get() = 4

	fun tile(vararg map: Int) = ArrayList<PointIntArrayList>().apply {
		for (z in 0 until 4) {
			add(PointIntArrayList().apply {
				val centerX = 3
				for (x in 0 until width) for (y in 0 until height)
					if (map[z * width * height + x + y * width] == 1) add(x - centerX, y - centerY)
			})
		}
	}

}

