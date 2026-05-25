# Vyshyvanka Editor - User Manual

A desktop application for creating Ukrainian embroidery patterns on a pixel grid. Each cell represents one stitch.

---

## Interface

The window has three areas:

- **Toolbar** (top) - main actions: New, Open File, Tile, Generate Name, Save as PNG, Undo, Redo
- **Sidebar** (left) - drawing tool, color palette, symmetry mode, grid size controls
- **Canvas** (center) - the pixel grid you draw on

---

## Drawing

Select **Draw** mode in the sidebar, pick a color from the palette, then click or drag on the canvas to paint cells. Select **Erase** to clear cells back to white.

The palette has 8 preset colors. For a custom color, use the color picker below the swatches - the preview square shows the currently active color.

---

## Symmetry

Symmetry mode mirrors your strokes in real time while drawing. Four options:

| Mode | Effect |
|---|---|
| None | Only the clicked cell is painted |
| Horizontal | Also paints the mirror cell across the same row |
| Vertical | Also paints the mirror cell across the same column |
| Both | Paints all four symmetric counterparts at once |

> **Tip:** Draw a small motif with *Both* symmetry active - it automatically forms a traditional four-way balanced ornament.

---

## Tiling

**Tile** copies a fragment across the entire canvas. Click the button, enter the width and height of your fragment (the piece you drew in the top-left corner), and confirm. The app fills the canvas by repeating that fragment, applying the current symmetry mode to alternate copies.

---

## Canvas Size

Enter new width and height values in the sidebar and click **Resize Grid**. Existing content is preserved. Limits: minimum 5 × 5, maximum 45 × 34.

---

## Generate from Name

**Generate Name** opens a list of pre-encoded name patterns. Selecting one clears the canvas and loads that pattern. Bundled names: Анастасія, Вячеслав, Оксана, Олена, Софія. Any PNG you place in the `images/` folder will also appear in the list.

---

## Open / Save

- **Open File** - loads a PNG from disk onto the canvas. Dark pixels become colored cells; near-white pixels are left empty.
- **Save as PNG** - exports the canvas as a PNG where each cell is one pixel.

---

## Undo / Redo

Use the toolbar buttons or `Ctrl+Z` / `Ctrl+Y`. Works for all operations including Tile, Resize, and Generate Name.

---

## Typical Workflow

1. Set canvas size in the sidebar
2. Pick a color and draw your motif in the top-left corner
3. Set a symmetry mode if needed
4. Click **Tile**, enter the fragment size, confirm
5. Touch up with Draw or Erase
6. Click **Save as PNG**