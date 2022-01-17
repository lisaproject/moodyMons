# ***moodyMons 情緒日記－AI 語音情緒辨識 APP***  

本專案是一款專為想要理解自己心情波動的人所設計之語音情緒辨識 APP。  
  ![](https://i.imgur.com/r1x9xqa.png)  
## 目錄  
* [***moodyMons*** 執行檔(apk)](#moodyMons-執行檔apk)  
* [操作示範影片](#操作示範影片)
* [使用手冊](#使用手冊)  
    * [安裝流程](#安裝流程)
    * [登入與登出](#登入與登出)
    * [紀錄情緒動態](#紀錄情緒動態)
    * [查看日記列表](#查看日記列表)
    * [查看周分析](#查看周分析)
    * [查看月分析](#查看月分析)
    * [查看諮詢資訊](#查看諮詢資訊)
* [專案開發流程](#專案開發流程)  
* [開發環境需求](#開發環境需求)  
* [開始開發](#開始開發)  

## ***moodyMons*** 執行檔(apk)  

QRCODE：  
![](https://i.imgur.com/agAQJH7.png)  
  
[apk 連結](https://drive.google.com/drive/folders/1DjHTmqfEk49wVjDKvDwENcIQFbs4r1Bi?usp=sharing)  

## 操作示範影片  
QRCODE：  
![](https://i.imgur.com/v682rdc.png)  


[影片連結](https://youtu.be/PlhWJmkOK-g)  

## 使用手冊  
### 安裝流程  
下載並點擊安裝檔進行安裝，完成後即可在手機應用程式中找到 ***moodyMons***，並點選「允許」來同意錄音與存取檔案的權限。  
![](https://i.imgur.com/f71hebR.png)  

![](https://i.imgur.com/vhx3UAk.png)  

### 登入與登出  
登入：按下「sign in with google」以 Google 帳戶來登入，進入 ***moodyMons***。  
  
 ![](https://i.imgur.com/WIAOxAn.png)  

登出：按下左上角的「登出」並在彈出式視窗確認登出，回到登入頁面。  

![](https://i.imgur.com/VwZwUPk.png)

### 紀錄情緒動態
在首頁按下錄音鍵開始錄音，錄音結束後再按錄音鍵，畫面會出現 ***moodyMons*** 判斷出這段音訊的情緒結果，可以在下方輸入簡短的文字（限20字）來記錄動態，按下完成，情緒動態就新增成功。
![](https://i.imgur.com/0hF46Zu.png)
若覺得 ***moodyMons*** 判斷的情緒有誤，也可以透過切換情緒來回饋，但在日記列表仍會顯示原本判斷的結果。
![](https://i.imgur.com/i4gA2Bh.png)
### 查看日記列表
可以透過點選下方列表的日記或左右滑動來到日記列表頁面，在這裡可以用上下滑動的方式瀏覽以日記形式依照時間排序的情緒動態。
![](https://i.imgur.com/CGL8bU6.png)

### 查看周分析
可以透過點選下方列表的周分析或左右滑動來到周分析頁面，點選右上角可以選擇畫面要顯示哪一周。
![](https://i.imgur.com/Tk4We05.png)

在頁面往下滑動可以看到在這周 的情緒分布比例，點開情緒圖卡可以查看依照情緒整理的該周動態。
![](https://i.imgur.com/t7otBS8.png)
### 查看月分析
可以透過點選下方列表的月分析或左右滑動來到月分析頁面，點選右上角可以選擇畫面要顯示哪一個月份，在頁面往下滑動可以看到在這個月每周主要發布動態的情緒。
![](https://i.imgur.com/r6wKeny.png)

### 查看諮詢資訊

可以透過點選下方列表的諮詢專線或左右滑動來到諮詢專線頁面，在這裡可以用上下滑動的方式瀏覽諮詢資訊。

![](https://i.imgur.com/3wnfdiU.png)

## 專案開發流程  
本專案以 [NNIME 語料庫](https://nnime.ee.nthu.edu.tw/) 為主軸，將音訊轉換成梅爾頻譜訓練出中文語音情緒辨識模型，把模型經由 [TensorFlow Lite](https://www.tensorflow.org/lite?hl=zh-tw) 轉換，應用於以使用者為中心開發的 ***moodyMons*** ， 讓模型在手機上運行，同時運用 [Firebase] 雲端儲存模型的辨識結果、使用者回饋與日記內容以評估模型。  
***moodyMons***  使用 [single-Activity](https://youtu.be/2k8x8V77CrU) 架構與 [Android Jetpack](https://developer.android.com/jetpack) 來開發穩健且容易維護的應用程式，藉由邊緣運算讓音訊不需上傳至雲端即可在裝置上辨識出情緒，是**即時且保有隱私**的語音情緒辨識應用。  

![](https://i.imgur.com/2IeqQ0y.png)  

## 開發環境需求  
**1. 相關技術**
* 語言使用 : Kotlin 1.3.72
* 開發工具 : Android Studio 4.2.2

**2. 軟體需求**

* Android 8.1 （API level 27）
* 建議使用 Android 8.1 以上版本的裝置

**3. 硬體需求** 

* 記憶體 100MB 以上
* 螢幕解析度 1920 x 1080pixels
* 建議使用 Nexus 5X 取得最佳的畫面體驗

**4. 網路需求** 
* 手機必須具有穩定傳輸的網路環境

## 開始開發  

clone [這份專案](https://github.com/lisaproject/moodyMons.git)，開啟 [Android Studio](https://developer.android.com/studio) 執行專案。 
