package flashcards
 
import java.io.File
import java.util.*
 
fun main(args: Array<String>){
    val scanner = Scanner(System.`in`)
    val flashcardsList = mutableMapOf<String, String>()
    val hardValue = mutableMapOf<String, String>()
    val logList = LinkedList<String>()
    var go = true
    var fileToSave = ""
    for (indexArg in 0..(args.size - 1)) {
        if(args[indexArg].equals("-import")) {
            val file = File(args[indexArg + 1])
            if(!file.exists()) {
                println("File not found.\n")
                logList.add("File not found.\n")
            }
            else {
                val listWords = file.readLines()
                for(index in listWords.indices step 3){
                    flashcardsList[listWords[index]] = listWords[index + 1]
                    hardValue[listWords[index]] = listWords[index + 2]
                }
                println("${file.readLines().size/3} cards have been loaded\n")
            }
        } else if (args[indexArg].equals("-export")) {
            fileToSave = args[indexArg + 1]
        }
    }
    while(go) {
        print(menu())
        logList.add(menu())
        val scelta = scanner.nextLine()
        logList.add(scelta)
        when (scelta) {
            "add" -> {
                println("The card:")
                logList.add("The card:")
                val term = scanner.nextLine()
                logList.add(term)
                if (flashcardsList.containsKey(term)) {
                    println("The card \"$term\" already exists.\n")
                    logList.add("The card \"$term\" already exists.\n")
                } else {
                    println("The definition of the card:")
                    logList.add("The definition of the card:")
                    val definition = scanner.nextLine()
                    logList.add(definition)
                    if (flashcardsList.containsValue(definition)) {
                        println("The definition \"$definition\" already exists.\n")
                        logList.add("The definition \"$definition\" already exists.\n")
                    } else {
                        flashcardsList[term] = definition
                        hardValue[term] = "0"
                        println("The pair (\"$term\":\"$definition\") has been added.\n")
                        logList.add("The pair (\"$term\":\"$definition\") has been added.\n")
                    }
                }
            }
            "remove" -> {
                println("The card:")
                logList.add("The card:")
                val removeFlashcard = scanner.nextLine()
                logList.add(removeFlashcard)
                if (flashcardsList.containsKey(removeFlashcard)){
                    flashcardsList.remove(removeFlashcard)
                    hardValue.remove(removeFlashcard)
                    println("The card has been removed.\n")
                    logList.add("The card has been removed.\n")
                } else {
                    println("Can't remove \"$removeFlashcard\": there is no such card.\n")
                    logList.add("Can't remove \"$removeFlashcard\": there is no such card.\n")
                }
            }
            "import" -> {
                println("File name:")
                logList.add("File name:")
                val file = File(scanner.nextLine())
                logList.add(file.name)
                if(!file.exists()) {
                    println("File not found.\n")
                    logList.add("File not found.\n")
                }
                else {
                    val listWords = file.readLines()
                    for(index in listWords.indices step 3){
                        flashcardsList[listWords[index]] = listWords[index + 1]
                        hardValue[listWords[index]] = listWords[index + 2]
                    }
                    println("${file.readLines().size/3} cards have been loaded\n")
                    logList.add("${file.readLines().size/3} cards have been loaded\n")
                }
 
            }
            "export" -> {
                println("File name:")
                logList.add("File name:")
                var s = ""
                val file = File(scanner.nextLine())
                logList.add(file.name)
                flashcardsList.forEach { t, u ->  s += "$t\n$u\n${hardValue[t]}\n"}
                file.writeText(s)
                println("${file.readLines().size/3} cards have been saved\n")
                logList.add("${file.readLines().size/3} cards have been saved\n")
            }
            "ask" -> {
                println("How many times to ask?")
                logList.add("How many times to ask?")
                val howManyTimes = scanner.nextInt()
                logList.add(howManyTimes.toString())
                scanner.nextLine()
                repeat(howManyTimes){
                    val card = flashcardsList.entries.shuffled().first()
                    println("Print the definition of \"${card.key}\":")
                    logList.add("Print the definition of \"${card.key}\":")
                    val answer = scanner.nextLine()
                    logList.add(answer)
                    if(answer == card.value) {
                        println("Correct answer\n")
                        logList.add("Correct answer\n")
                    }
                    else if(flashcardsList.containsValue(answer)){
                        println("Wrong answer. The correct one is \"${card.value}\", you've just written the definition of \"${flashcardsList.filterValues { it == answer }.keys.first()}\".\n")
                        logList.add("Wrong answer. The correct one is \"${card.value}\", you've just written the definition of \"${flashcardsList.filterValues { it == answer }.keys.first()}\".\n")
                        hardValue[card.key] = (hardValue[card.key]!!.toInt() + 1).toString()
                    }
                    else{
                        println("Wrong answer. The correct one is \"${card.value}\".")
                        logList.add("Wrong answer. The correct one is \"${card.value}\".")
                        hardValue[card.key] = (hardValue[card.key]!!.toInt() + 1).toString()
                    }
                }
                println()
                logList.add("\n")
            }
            "log" -> {
                println("File name:")
                logList.add("File name:")
                val file = File(scanner.nextLine())
                logList.add(file.name)
                var s = ""
                logList.forEach { t -> s += t + "\n" }
                file.writeText(s)
                println("The log has been saved.\n")
                logList.add("The log has been saved.\n")
            }
            "hardest card" -> {
                var check = false
                var s = ""
                var count = 0
                if( hardValue.values.filter { !it.equals("0")}.isEmpty()){
                    println("There are no cards with errors.")
                    logList.add("There are no cards with errors.")
                } else {
                    var max = hardValue.maxBy { it.value }?.value
                    hardValue.forEach { t, u ->  if (u == max) count += 1 }
                    if (count == 1) {
                        println("The hardest card is \"${hardValue.filterValues { it == max}.keys.first()}\". You have $max errors answering it.")
                        logList.add("The hardest card is \"${hardValue.filterValues { it == max}.keys.first()}\". You have $max errors answering it.")
                    } else {
                        for ( (k, v) in hardValue){
                            if (count > 1) {
                                s += "\"$k\", "
                                count--
                            } else {
                                s += "\"$k\""
                            }
                        }
                        println("The hardest cards are $s. You have $max errors answering them.")
                        logList.add("The hardest cards are $s. You have $max errors answering them.")
                    }
 
                }
                println()
                logList.add("\n")
            }
            "reset stats" -> {
                hardValue.keys.forEach { hardValue[it] = "0"}
                println("Card statistics has been reset.\n")
                logList.add("Card statistics has been reset.\n")
            }
            "exit" -> {
                println("Bye bye!")
                logList.add("Bye bye!")
                if(!fileToSave.equals("")){
                    var s = ""
                    val file = File(fileToSave)
                    flashcardsList.forEach { t, u ->  s += "$t\n$u\n${hardValue[t]}\n"}
                    file.writeText(s)
                    println("${file.readLines().size/3} cards have been saved\n")
                    logList.add("${file.readLines().size/3} cards have been saved\n")
                }
                go = false
            }
        }
    }
}
 
fun menu():String {
    return "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n"
}
