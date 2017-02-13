# LetterView



有一个选中字母完成单词的需求，需求大概如下图

![letterview.jpg](https://github.com/dengzq/LetterView/blob/master/image/letterview.jpg)


因此写了一个类似功能的字母选择控件，贴上完成效果

![letterview.gif](https://github.com/dengzq/LetterView/blob/master/image/letterView.gif)


##使用方法

直接在xml文件中引用



```
<com.dengzq.letterview.widget.LetterGridView

        android:layout_centerInParent="true"

        app:letterHorizontalMargin="20"

        app:letterVerticalMargin="20"

        app:letterRadius="60"

        app:letterColumn="12"

        app:letterRow="10"

        app:textSize="12"

        app:textCheckColor="#FFFFFF"

        android:background="#FFE4E1"

        app:strokeFinishWidth="2"

        android:id="@+id/lgl"

        android:layout_width="wrap_content"

        android:layout_height="wrap_content"

        android:layout_marginTop="30dp"/>
```

传递单词 setWords(String word)  


<br/>
##相关属性

| 属性    | 描述    |
|--------|:--------:|
|     letterViewSize   |   字母控件的大小     |
|     letterColumn   |   列数     |
|     letterRow   |   行数     |
|     letterHorizontalMargin   |   字母间的水平间距     |
|     letterVerticalMargin   |   字母间的竖直间距     |
|     strokeFinishWidth   |   完成时边框宽度     |
|     strokeFinishColor   |   完成时边框颜色    |
|     strokeWidth   |   默认边框宽度    |
|     strokeColor   |   默认边框颜色    |
|     textSize   |   文字大小    |
|     textDefaultColor   |   文字默认颜色    |
|     textCheckColor   |   文字选中颜色    |
|     textFinishColor   |   文字完成颜色    |
|     checkedColor   |   选中时背景颜色    |

###end


