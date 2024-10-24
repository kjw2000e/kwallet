package com.example.kwallet

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.testviewpager.Page
import com.example.testviewpager.ViewPagerAdapter
import kotlin.math.abs

class MainActivity2 : AppCompatActivity() {
    val OFF_SCREEN_PAGE_LIMIT = 2
    val OFF_SCREEN_PAGE_RATIO = 0.9f



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        val viewpager2 = findViewById<ViewPager2>(R.id.viewpager2)
        val documentAdapter = ViewPagerAdapter(
            listOf(
                Page("안녕하세요", "#FFFFFF", 1),
                Page("이것은 테스트입니다", "#FFFF00", 2),
                Page("자 잘넘어가나 볼까요", "#00FFFF", 3),
                Page("옴마마", "#FF00FF", 4),
                Page("아아아아아아", "#CCCCCC", 5)
            ))
        viewpager2.offscreenPageLimit = 1
        viewpager2.adapter = documentAdapter

        viewpager2.setPageTransformer(
            CompositePageTransformer().apply {
                addTransformer(ViewPager2.PageTransformer{ view: View, position: Float ->
                    var v = 1 - abs(position)
                    view.apply {
                        // Adding rounded corners and shadow with CardView properties
                        if (this is androidx.cardview.widget.CardView) {
                            radius = 16f
                            cardElevation = 8 * v
                        }

                        // Scaling and fading effect for the transition
                        scaleY = OFF_SCREEN_PAGE_RATIO + (1 - OFF_SCREEN_PAGE_RATIO) * v
                        alpha = 0.5f + (0.5f * v) // Fade out as it goes off screen

                        // Adjust the X-axis for a smooth slide transition
                        translationX = -50 * position
                    }

                    if(position < 0){
                        // 왼쪽에 있는 페이지
                        view.transitionAlpha = v // 점점 투명도를 높여주기
                    } else {
                        // 오른쪽에 있는 페이지
                        view.x = -50 * v // N+i 번째는 오른쪽으로 (50 x i) 만큼 이동
                        view.elevation = v // i 가 작을 수록 윗 장으로 올라오도록 하기
                        view.scaleY = OFF_SCREEN_PAGE_RATIO + (1-OFF_SCREEN_PAGE_RATIO) * v // 뒷 장을 갈수록 크기의 비율
                        view.transitionAlpha = OFF_SCREEN_PAGE_LIMIT + v // 제일 끝 장 없어질 때 fade out 으로 없어지기
                        viewpager2.offscreenPageLimit = OFF_SCREEN_PAGE_LIMIT // LIMIT 개수 제외 나머지 안보이게 하기
                    }
                })
            }
        )

        viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                documentAdapter.updateCurrentPage(position)
            }
        })
    }
}