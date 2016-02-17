package com.mygdx.game;

        import com.badlogic.gdx.graphics.g2d.SpriteBatch;

        import javax.swing.GroupLayout;


/**
 * Created by Keith on 10/25/2015.
 */
public class EnemyList{

    private LilyPadManager lilyPadList;
    private Alligator []alligators;
    private Bird bird;
    private EnemyFrog enemyFrog;
    private String alligatorImg, birdImg;
    private MyObjectList<Enemy> attackingEnemies;
    private int numAtking;
    private MyObjectList<PlayerFrog> frogs;
    private MyObjectList<EnemyFrog> enemyFrogs;
    private Frog players;
    private Enemy curEnemy;
    private int numActiveEnemyFrogs;
    private float sentEnemtAtTime;



    public EnemyList(PlayerFrog player, LilyPadManager lpm, float alligatorSpeed , float alligatorRange, float alligatorTimeBeforeAtk){
        this.players = player;
        frogs = new MyObjectList<PlayerFrog>();
        enemyFrogs = new MyObjectList<EnemyFrog>(); // List of enemy frogs that will be drawn

        frogs.Add(player);

        lilyPadList = lpm;

        alligatorImg = "";
        alligators = new Alligator[lilyPadList.getPadArr().length];
        birdImg = "TEST.png";
        bird = new Bird(100, 2, birdImg, lilyPadList.getPadArr().length);

        numAtking = 0;
        numActiveEnemyFrogs = 0;
        attackingEnemies = new MyObjectList<Enemy>();
        // Create an enemy for each pad
        for(int i = 0; i < lilyPadList.getPadArr().length - 1; i++){
            alligators[i] = new Alligator(alligatorSpeed, alligatorRange,  lilyPadList.getPadArr()[i], alligatorImg, alligatorTimeBeforeAtk);
        }

    }

    public boolean SendAlligator (int sendEnemy){
        boolean wasSent;
        if(sendEnemy < alligators.length && sendEnemy >= 0) {
            if(alligators[sendEnemy].isBeingUsed){
                System.err.println("This alligator is already attacking");
                wasSent = false;
            }
            else {
                if (lilyPadList.GetNumPadsAvail() > 1) { // We need to leave at least 1 pad for the frog to sit on
                    alligators[sendEnemy].isBeingUsed = true;
                    attackingEnemies.Add(alligators[sendEnemy]);
                    wasSent = true;
                } else {
                    System.err.println("Cannot send this alligator because too many alligators are already attacking");
                    wasSent = false;
                }
            }
        }
        else{
            System.err.println("Index out of bounds on alligator array");
            wasSent = false;
        }

        return wasSent;
    }

    public void SendBird( boolean fromLeft){
        attackingEnemies.Add(bird);
        //if(!bird.isBeingUsed){
        bird.UpdateTargets(frogs, enemyFrogs);
        //}
        bird.isBeingUsed = true;
        bird.startAtk(fromLeft);

    }

    public void SendFrog(int padNum, float maxStamnia, float difficulty){
        // maxStamina is how much stamina the frog starts with and its stamina cannot exceed this amount
        // difficulty relates to how accurate and how often this frog grabs food. It is expected to be a value between 1 and 0 including 1 and 0.
        if(enemyFrogs.GetSize() < 3) {
            String frogImage;
            if(difficulty < 0){
                difficulty = 0;
            }
            if(difficulty > 1){
                difficulty = 1;
            }

            // Change the frog image based on difficulty
            if(difficulty < .3){
                frogImage = "Frog.png";
            }
            else if(difficulty < .6){
                frogImage = "Frog.png";
            }
            else{
                frogImage = "Frog.png";
            }
            enemyFrog = new EnemyFrog(10, 1, 2, 200, maxStamnia, 10, frogImage, lilyPadList, GameScreen.allFood, difficulty);//constant arguments are (in order): Diameter of the image, tongue width, tongue tip radius, tongue speed
            enemyFrogs.Add(enemyFrog);
            enemyFrog.SetPad(padNum);
        }
    }

    public void SendEnemies(float difficulty, float howOftenToSend){
        int padNum = players.myCurPad;
        // howOftenToSend is the measurement in seconds of how often we "might" send an enemy
        if(Math.random() < .1 && GameScreen.CURTIME - sentEnemtAtTime > howOftenToSend){ //we have a 10% chance of sending an enemy
            sentEnemtAtTime = GameScreen.CURTIME;
            if(lilyPadList.GetNumPadsAvail() > 2 && Math.random() > .5f) {//Only send frogs and alligators if there are more than 2 pads available, Also add an element of chance since we do not want to be able to send birds at any time as well
                if (Math.random() > .5f && !alligators[padNum].isBeingUsed){// 50% chance of getting a frog or an alligator
                    SendAlligator(padNum);
                }
                else{
                    System.out.println("SendFrog:(MAYBE)  1");
                    if(padNum == players.myCurPad){
                        if(padNum <= Math.floor(lilyPadList.getPadArr().length/2f)){
                            padNum = lilyPadList.getPadArr().length - 1;
                        }
                        else{
                            padNum = 0;
                        }
                    }

                    if(lilyPadList.getPadArr()[padNum].GetFrogOnPad() == null && !lilyPadList.getPadArr()[padNum].GetIsGone()) {
                        System.out.println("SendFrog:(MAYBE)  2");
                        if(alligators[padNum] != null) {
                            System.out.println("SendFrog:(MAYBE)  3");
                            if (alligators[padNum].GetTargetPad() != lilyPadList.getPadArr()[padNum]) {//Make sure the pad is not currently target by an alligator
                                System.out.println("SendFrog:(MAYBE)  4");
                                SendFrog(padNum, 10, difficulty);
                            }
                        }
                    }
                }
            }
            else{
                if(!bird.isBeingUsed){
                    SendBird(Math.random() < .5); // Decided if the bird is coming from the left or the right 50/50 chance either way
                }
            }
        }
    }

    public void Draw(SpriteBatch sb) {
        if(attackingEnemies.GetSize() > 0) {
            for (MyNode<Enemy> curEnemy = attackingEnemies.GetHead(); curEnemy != null; curEnemy = curEnemy.GetNext()) {

                if (!curEnemy.GetObject().isBeingUsed) { // If the enemy has set itself to no longer being used in it's draw method then it is no longer attacking and can be removed from this list
                    attackingEnemies.Remove(curEnemy.GetObject());
                } else {
                    curEnemy.GetObject().Draw(sb);
                }
            }
        }
    }

    public void DrawEnemyFrogsOnLayer(int layerNum, SpriteBatch sb){
        if(enemyFrogs.GetSize() > 0) {
            for (MyNode<EnemyFrog> curEnemy = enemyFrogs.GetHead(); curEnemy != null; curEnemy = curEnemy.GetNext()) {
                if(curEnemy.GetObject().GetIsAlive()) {
                    if(curEnemy.GetObject().layer == layerNum) {
                        curEnemy.GetObject().DrawFrog(sb);
                    }
                }
                else{
                    enemyFrogs.Remove(curEnemy.GetObject());
                }
            }
        }

    }

    public void Reset(){

    }


}
