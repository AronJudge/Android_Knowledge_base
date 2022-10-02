# 自定义UI实战

ViewGroup为什么不会执行onDraw()

View.draw(canvas) (DecorView)
    onDraw(canvas)
    dispatchDraw(canvas) (ViewGroup.dispatchDraw)
        drawChild view.draw(Canvas canvas, ViewGroup parent long drawingTime)
                - renderNode = updateDisplayListIfDirty()
                        dispatchDraw()