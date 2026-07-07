package pvp.client.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Tiny 8x8 pixel-art icons drawn with plain fills, one per module. */
public final class PixelIcon {
	private PixelIcon() {
	}

	public static String[] forModule(String moduleId) {
		return switch (moduleId) {
			case "fps" -> new String[] {
				"...YY...",
				"..YYY...",
				".YYY....",
				"YYYYYY..",
				"..YYY...",
				".YYY....",
				"YYY.....",
				"Y......."
			};
			case "ping" -> new String[] {
				".......G",
				".......G",
				".....G.G",
				".....G.G",
				"...G.G.G",
				"...G.G.G",
				".G.G.G.G",
				".G.G.G.G"
			};
			case "effects" -> new String[] {
				"..gWWg..",
				"...WW...",
				"..KPPK..",
				".KPPPPK.",
				"KPPpPPPK",
				"KPPPPPPK",
				".KPPPPK.",
				"..KKKK.."
			};
			case "armor" -> new String[] {
				"CC....CC",
				"CCC..CCC",
				"CCCWWCCC",
				".CCCCCC.",
				".CCCCCC.",
				".CCCCCC.",
				".CCCCCC.",
				"........"
			};
			case "zoom" -> new String[] {
				".KKKK...",
				"KCCWCK..",
				"KCWCCK..",
				"KCCCCK..",
				".KKKKg..",
				".....gg.",
				"......gg",
				"........"
			};
			case "viewmodel" -> new String[] {
				"......CC",
				".....CCC",
				"....CCC.",
				"...CCC..",
				"b.CCC...",
				".bCC....",
				"bbb.....",
				"b.b....."
			};
			case "hitcolor" -> new String[] {
				".RR..RR.",
				"RWRRRRRR",
				"RRRRRRRR",
				"RRRRRRRR",
				".RRRRRR.",
				"..RRRR..",
				"...RR...",
				"........"
			};
			case "togglesprint" -> new String[] {
				"........",
				"O...O...",
				"OO..OO..",
				"OOO.OOO.",
				"OO..OO..",
				"O...O...",
				"........",
				"........"
			};
			case "freelook" -> new String[] {
				"........",
				"..WWWW..",
				".W....W.",
				"W..BB..W",
				"W..BB..W",
				".W....W.",
				"..WWWW..",
				"........"
			};
			case "atmosphere" -> new String[] {
				"...ww...",
				"..wwww..",
				".wwwwww.",
				"wwwwwwww",
				"wwwwwwww",
				".B..B...",
				"..B..B.B",
				".B..B..."
			};
			default -> new String[] {
				"WWWWWWWW",
				"W......W",
				"W.WWWW.W",
				"W.W..W.W",
				"W.W..W.W",
				"W.WWWW.W",
				"W......W",
				"WWWWWWWW"
			};
		};
	}

	private static int color(char c) {
		return switch (c) {
			case 'W' -> 0xFFFFFFFF;
			case 'w' -> 0xFFC6C6C6;
			case 'g' -> 0xFF8B8B8B;
			case 'K' -> 0xFF1B1B24;
			case 'G' -> 0xFF3FB950;
			case 'R' -> 0xFFE5484D;
			case 'Y' -> 0xFFFFD33D;
			case 'O' -> 0xFFF0883E;
			case 'B' -> 0xFF4C8DFF;
			case 'C' -> 0xFF39C5CF;
			case 'P' -> 0xFF9E6BFF;
			case 'p' -> 0xFFFF6BD5;
			case 'b' -> 0xFF8B5A2B;
			default -> 0;
		};
	}

	public static void draw(GuiGraphicsExtractor graphics, String[] rows, int x, int y, int pixelSize) {
		for (int row = 0; row < rows.length; row++) {
			String line = rows[row];
			for (int col = 0; col < line.length(); col++) {
				int argb = color(line.charAt(col));
				if (argb != 0) {
					int px = x + col * pixelSize;
					int py = y + row * pixelSize;
					graphics.fill(px, py, px + pixelSize, py + pixelSize, argb);
				}
			}
		}
	}
}
