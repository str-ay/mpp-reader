package pro.jness.mppreader.utils;

import net.sf.mpxj.Relation;
import org.apache.commons.collections4.CollectionUtils;
import pro.jness.mppreader.utils.mpp.Task;
import pro.jness.mppreader.utils.mpp.TaskBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public static BigDecimal bd(Number number) {
        if (number == null) {
            return null;
        }

        return new BigDecimal(number.toString());
    }

    public static LocalDateTime date(Date date) {
        if (date == null) {
            return null;
        }

        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    public static Task.Duration duration(net.sf.mpxj.Duration duration) {
        if (duration == null) {
            return null;
        }

        return new Task.Duration(duration.getUnits(), duration.getDuration());
    }

    public static Task taskWithChildren(net.sf.mpxj.Task task, List<net.sf.mpxj.Task> all) {
        Task resultTask = task(task);
        addChildren(resultTask, all);

        return resultTask;
    }

    public static Task task(net.sf.mpxj.Task task) {
        Task result = new TaskBuilder()
                .setName(task.getName())
                .setID(task.getID())
                .setUniqueID(task.getUniqueID())
                .setTaskMode(task.getTaskMode())
                .setDuration(Utils.duration(task.getDuration()))

                .setActive(task.getActive())
                .setActualCost(Utils.bd(task.getActualCost()))
                .setActualDuration(Utils.duration(task.getActualDuration()))
                .setActualStart(Utils.date(task.getActualStart()))
                .setActualFinish(Utils.date(task.getActualFinish()))
                .setActualCost(Utils.bd(task.getActualCost()))
                .setActualOvertimeCost(Utils.bd(task.getActualOvertimeCost()))
                .setActualOvertimeWork(Utils.duration(task.getOvertimeWork()))
                .setActualOvertimeWorkProtected(Utils.duration(task.getActualOvertimeWorkProtected()))

                .setEarlyStart(Utils.date(task.getEarlyStart()))
                .setEarlyFinish(Utils.date(task.getEarlyFinish()))

                .setStart(Utils.date(task.getStart()))
                .setStartText(task.getStartText())
                .setStartSlack(Utils.duration(task.getStartSlack()))
                .setStartVariance(Utils.duration(task.getStartVariance()))
                .setFinish(Utils.date(task.getFinish()))
                .setFinishText(task.getFinishText())
                .setFinishSlack(Utils.duration(task.getFinishSlack()))
                .setFinishVariance(Utils.duration(task.getFinishVariance()))

                .createTask();

        if (CollectionUtils.isNotEmpty(task.getPredecessors())) {
            result.setPredecessors(new ArrayList<>(task.getPredecessors().size()));

            for (Relation relation : task.getPredecessors()) {
                result.getPredecessors().add(Utils.predecessor(relation));
            }
        }

        return result;
    }

    public static Task.Predecessor predecessor(net.sf.mpxj.Relation relation) {
        Task.Predecessor predecessor = new Task.Predecessor();
        predecessor.setRelationType(relation.getType());

        if (relation.getTargetTask() != null) {
            predecessor.setTargetTaskID(relation.getTargetTask().getID());
        }

        if (relation.getSourceTask() != null) {
            predecessor.setSourceTaskID(relation.getSourceTask().getID());
        }

        return predecessor;
    }

    public static void addChildren(pro.jness.mppreader.utils.mpp.Task task, List<net.sf.mpxj.Task> all) {
        List<pro.jness.mppreader.utils.mpp.Task> children = new ArrayList<>();
        for (net.sf.mpxj.Task t : all) {
            if (t.getParentTask() == null) {
                continue;
            }

            if (t.getParentTask().getID().equals(task.getID())) {
                pro.jness.mppreader.utils.mpp.Task child = Utils.task(t);
                addChildren(child, all);
                children.add(child);
            }
        }

        task.setChildTasks(children);
    }
}
