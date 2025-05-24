# YETIFY - Spotify Clone

YETIFY is a minimalist Android music player dedicated exclusively to local tracks by Ye (Kanye West)
It features a clean UI, smooth playback, and a curated experience for fans of his "bangers"

## Features

- Offline playback of selected Ye tracks 
- Elegant dark UI inspired by modern streaming platforms like spotify iykyk
- Simple Media player controls

## Included Tracks

- Cousins  
- WW3  
- HH 

## Requirements

- Android 10.0 (API 29) or higher
- Local `.mp3` files placed in the `res/raw/` directory

## Getting Started

1. Clone the repository  
2. Place the `.mp3` files into `app/src/main/res/raw/`  
3. Build and run the app via Android Studio

## How to add custom songs on the project?

1. Read the `## Getting Started` and build the project
2. On the `MainActivity.kt`, scroll down until you see `val songs = listOf()`
3. Press enter and go to the next line, add your song by using `Song("SongName", "artist name", R.raw.songpathinsiderawfolder)`
4. That's all!

## License 

This project is for personal and educational use only.  
All rights to the music belong to their respective owners.
