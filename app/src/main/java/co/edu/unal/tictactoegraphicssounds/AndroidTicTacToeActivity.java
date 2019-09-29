package co.edu.unal.tictactoegraphicssounds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private final static int BOARD_SIZE = 9;
    private TicTacToeGame mGame;
    private boolean mGameOver;
    private TextView mHumanRes;
    private TextView mTiesRes;
    private TextView mAndroidRes;
    private int numHumanWon;
    private int numTies;
    private int numAndroidWon;
    private int numPlayedGames;
    private boolean androidFirst;
    private boolean humanFirst;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;
    private BoardView mBoardView;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    Handler handler = new Handler();



    private int mSelectedIndex;
    ImageView image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanRes = (TextView) findViewById(R.id.humanRes);
        mTiesRes = (TextView) findViewById(R.id.tiesRes);
        mAndroidRes = (TextView) findViewById(R.id.androidRes);

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        numHumanWon = 0;
        numTies = 0;
        numAndroidWon = 0;
        numPlayedGames = 1;
        mHumanRes.setText(R.string.human_won + numHumanWon);
        mTiesRes.setText(R.string.num_ties + numTies);
        mAndroidRes.setText(R.string.android_won + numAndroidWon);
        mBoardView.setOnTouchListener(mTouchListener);
        startNewGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sword);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.swish);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }


    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();

        if(numPlayedGames % 2 == 0){
            mInfoTextView.setText(R.string.first_android);
            androidFirst = true;
            int move = mGame.getComputerMove();
            mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mBoardButtons[move].setEnabled(false);
            mBoardButtons[move].setText(String.valueOf(TicTacToeGame.COMPUTER_PLAYER));
            mBoardButtons[move].setTextColor(Color.rgb(200,0,0));
            mInfoTextView.setText(R.string.turn_human);
        }else{
            mInfoTextView.setText(R.string.first_human);
            humanFirst = true;
        }

        mGameOver = false;
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{

                //If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if(winner == 0){
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mInfoTextView.setText(R.string.turn_computer);
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);

                        }
                    }, 2000);
                    winner = mGame.checkForWinner();

                }

                if(winner == 0){
                    mInfoTextView.setText(R.string.turn_human);
                }else if(winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    numTies++;
                    mTiesRes.setText(getResources().getString(R.string.num_ties) + numTies);
                    mGameOver = true;
                    numPlayedGames++;
                }else if(winner == 2){
                    mInfoTextView.setText(R.string.result_human_wins);
                    numHumanWon++;
                    mHumanRes.setText(getResources().getString(R.string.human_won) + numHumanWon);
                    mGameOver = true;
                    numPlayedGames++;
                }else{
                    mInfoTextView.setText(R.string.result_computer_wins);
                    numAndroidWon++;
                    mAndroidRes.setText(getResources().getString(R.string.android_won) + numAndroidWon);
                    mGameOver = true;
                    numPlayedGames++;
                }

            }

// So we aren't notified of continued events when finger is moved
            return false;
        }

        private boolean setMove(char player, int location) {
            if(player == TicTacToeGame.HUMAN_PLAYER){
                mHumanMediaPlayer.start();
            }else{
                mComputerMediaPlayer.start();
            }

            if (mGame.setMove(player, location)) {
                mBoardView.invalidate();   // Redraw the board
                return true;
            }
            return false;
        }

    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.

                builder.setSingleChoiceItems(levels, mSelectedIndex,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog

                                // TODO: Set the diff level of mGame based on which item was selected.
                                switch (item){
                                    case 0:
                                        mGame.setmDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        break;

                                    case 1:
                                        mGame.setmDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        break;

                                    case 2:
                                        mGame.setmDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        break;
                                }

                                startNewGame();

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;

            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();


                break;
        }

        return dialog;
    }



    private class ButtonClickListener implements View.OnClickListener{
        int location;

        public ButtonClickListener(int location){
            this.location = location;
        }

        public void onClick(View view){

            if(!mGameOver){
                if(mBoardButtons[location].isEnabled()){

                    setMove(TicTacToeGame.HUMAN_PLAYER,location);

                    //If no winner yet, let the computer make a move
                    int winner = mGame.checkForWinner();
                    if(winner == 0){
                        mInfoTextView.setText(R.string.turn_computer);
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }

                    if(winner == 0){
                        mInfoTextView.setText(R.string.turn_human);
                    }else if(winner == 1){
                        mInfoTextView.setText(R.string.result_tie);
                        numTies++;
                        mTiesRes.setText(getResources().getString(R.string.num_ties) + numTies);
                        mGameOver = true;
                        numPlayedGames++;
                    }else if(winner == 2){
                        mInfoTextView.setText(R.string.result_human_wins);
                        numHumanWon++;
                        mHumanRes.setText(getResources().getString(R.string.human_won) + numHumanWon);
                        mGameOver = true;
                        numPlayedGames++;
                    }else{
                        mInfoTextView.setText(R.string.result_computer_wins);
                        numAndroidWon++;
                        mAndroidRes.setText(getResources().getString(R.string.android_won) + numAndroidWon);
                        mGameOver = true;
                        numPlayedGames++;
                    }
                }

            }

        }

        private void setMove(char player, int location){
            mGame.setMove(player, location);
            if(player == TicTacToeGame.HUMAN_PLAYER){
                mHumanMediaPlayer.start();
            }else{
                mComputerMediaPlayer.start();
            }
        }


    }


}
