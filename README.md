# ***moodyMons*** 使用說明  

## 簡介  
你經常覺得心情低落卻不知道自己怎麼了嗎？那麼，快來使用 moodyMons！讓我聽懂你的心~~~  

moodyMons 是一款專為想要理解自己心情波動的人所設計之語音情緒辨識 APP，結合邊緣運算與人工智慧，提供行動裝置語音即時辨識情緒的功能。你只需要對 moodyMons 開口，就能馬上得知當下的情緒，還可瀏覽周分析與月分析圖表，追蹤長期的情緒變化，讓可愛的情緒怪獸陪著你，一起學習與情緒共處吧！  
  ![](https://i.imgur.com/r1x9xqa.png)  
## 目錄  
* [***moodyMons*** 執行檔(apk)](#moodyMons-執行檔apk)  
* [宣傳影片](#宣傳影片)
* [操作示範影片](#操作示範影片)
* [使用手冊](#使用手冊)  
    * [安裝流程](#安裝流程)
    * [登入與登出](#登入與登出)
    * [紀錄情緒動態](#紀錄情緒動態)
    * [查看日記列表](#查看日記列表)
    * [查看周分析](#查看周分析)
    * [查看月分析](#查看月分析)
    * [查看諮詢資訊](#查看諮詢資訊)
* [作品開發流程](#作品開發流程)  
* [環境需求](#環境需求)  
## ***moodyMons*** 執行檔(apk)  

QRCODE：  
![](https://i.imgur.com/E8IlJEb.png)

  
[apk 連結](https://drive.google.com/drive/folders/1hCfK-SRJSyV4yFDkaax-4UHaR8P8Tq4n?usp=sharing)  
## 宣傳影片  

QRCODE：  
![](https://i.imgur.com/tDBhPNi.png)
[影片連結](https://youtu.be/edo2S2pQgjU)

## 操作示範影片  
QRCODE：  
![](https://i.imgur.com/v682rdc.png)  


[影片連結](https://youtu.be/PlhWJmkOK-g)  
## 使用手冊  
### 安裝流程  
下載並點擊安裝檔進行安裝，完成後即可在手機應用程式中找到 ***moodyMons***，並點選「允許」來同意錄音與存取檔案的權限。  
![](https://i.imgur.com/tPPtXpu.png)
![](https://i.imgur.com/ijsrh78.jpg)
### 登入與登出  
登入：按下「使用 Google 帳號登入」以 Google 帳戶來登入，進入 ***moodyMons***。  

![](https://i.imgur.com/13DnWoh.png)



登出：按下左上角的「登出」並在彈出式視窗確認登出，回到登入頁面。  

![](https://i.imgur.com/IvRkUhg.jpg)


### 紀錄情緒動態
在首頁按下錄音鍵開始錄音，錄音結束後再按錄音鍵，畫面會出現 ***moodyMons*** 判斷出這段音訊的情緒結果，可以在下方輸入簡短的文字（限20字）來記錄動態，按下完成，情緒動態就新增成功。
![](https://i.imgur.com/cg5HKV0.png)

若覺得 ***moodyMons*** 判斷的情緒有誤，也可以透過切換情緒來回饋，但在日記列表仍會顯示原本判斷的結果。
![](https://i.imgur.com/JdzU5zO.png)

### 查看日記列表
可以透過點選下方列表的日記或左右滑動來到日記列表頁面，在這裡可以用上下滑動的方式瀏覽以日記形式依照時間排序的情緒動態。
![](https://i.imgur.com/o5K2qEU.png)


### 查看周分析
可以透過點選下方列表的周分析或左右滑動來到周分析頁面，點選右上角可以選擇畫面要顯示哪一周。
![](https://i.imgur.com/p4ZqaTV.png)


在頁面往下滑動可以看到在這周的情緒分布比例，點開情緒圖卡可以查看依照情緒整理的該周動態。
![](https://i.imgur.com/9ggiRzW.png)

### 查看月分析
可以透過點選下方列表的月分析或左右滑動來到月分析頁面，點選右上角可以選擇畫面要顯示哪一個月份，在頁面往下滑動可以看到在這個月每周主要發布動態的情緒。
![](https://i.imgur.com/wlmZpAl.jpg)


### 查看諮詢資訊

可以透過點選下方列表的諮詢專線或左右滑動來到諮詢專線頁面，在這裡可以用上下滑動的方式瀏覽諮詢資訊。

![](https://i.imgur.com/PsCDRqI.jpg)


## 作品開發流程  
本作品以 [NNIME 語料庫](https://nnime.ee.nthu.edu.tw/) 為主軸，將音訊轉換成梅爾頻譜訓練出中文語音情緒辨識模型，把模型經由 [TensorFlow Lite](https://www.tensorflow.org/lite?hl=zh-tw) 轉換，應用於以使用者為中心開發的 ***moodyMons*** ， 讓模型在手機上運行，同時運用 [Firebase](https://firebase.google.com) 雲端儲存模型的辨識結果、使用者回饋與日記內容以評估模型。  
***moodyMons***  使用 [single-Activity](https://youtu.be/2k8x8V77CrU) 架構與 [Android Jetpack](https://developer.android.com/jetpack) 來開發穩健且容易維護的應用程式，藉由邊緣運算讓音訊不需上傳至雲端即可在裝置上辨識出情緒，是**即時且保有隱私**的語音情緒辨識應用。  

![](https://i.imgur.com/2IeqQ0y.png)  

## 環境需求  
**1. 相關技術**
* 語言使用 : Kotlin 1.3.72
* 開發工具 : Android Studio 4.2.2

**2. 軟體需求**

* Android 8.1 （API level 27）
* 建議使用 Android 8.1 以上版本的裝置

**3. 硬體需求** 

* 記憶體 400MB 以上
* 螢幕解析度 1920 x 1080pixels
* 建議使用 Nexus 5X 取得最佳的畫面體驗

**4. 網路需求** 
* 手機必須具有穩定傳輸的網路環境
