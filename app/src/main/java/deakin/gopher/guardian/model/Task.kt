package deakin.gopher.guardian.model

class Task {

    var Task_Name: String? = "";
    var Description: String? = "";


    constructor(taskId: String?, description: String?, ) {

        this.Task_Name = taskId
        this.Description = description

    }
    constructor()
    fun gettaskId(): String? {
        return this.Task_Name
    }
    fun getdescription(): String? {
        return this.Description
    }

    override fun toString(): String {
        return (
                "Add_Task{" +
                        "Task_Name='" +
                        Task_Name +
                        '\'' +
                        ", Description='" +
                        Description +
                        '\'' +
                        '}'
                )
    }

}
