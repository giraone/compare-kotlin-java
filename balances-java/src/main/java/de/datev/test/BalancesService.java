package de.datev.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.maxBy;

public class BalancesService {

    public BigDecimal sumLatestTotalsByIbanImperativeOriginalCode(List<DailyBalance> dailyBalances) {
        var acc = BigDecimal.ZERO;
        var balancesGroupedByIban = new HashMap<String, List<DailyBalance>>();
        for (var balance : dailyBalances) {
            balancesGroupedByIban
                .computeIfAbsent(balance.getIban(), k -> new ArrayList<>())
                .add(balance);
        }
        for (var balances : balancesGroupedByIban.values()) {
            DailyBalance latestDailyBalance = null;
            for (var balance : balances) {
                if (latestDailyBalance == null) {
                    latestDailyBalance = balance;
                }
                else if (balance.getDay().isAfter(latestDailyBalance.getDay())) {
                    latestDailyBalance = balance;
                }
            }
            var total = Objects.requireNonNull(latestDailyBalance).getTotal();
            acc = acc.add(total);
        }
        return acc;
    }

    public BigDecimal sumLatestTotalsByIbanImperativeWithKnownIbans(List<DailyBalance> dailyBalances, Set<String> ibansOfCustomer) {

        int numberOfAccounts = ibansOfCustomer.size();
        Set<String> considered = new HashSet<>(ibansOfCustomer.size(), 1.0f);
        BigDecimal ret = BigDecimal.ZERO;
        int index = dailyBalances.size() - 1;
        while (considered.size() < numberOfAccounts && index >= 0) {
            DailyBalance dailyBalance = dailyBalances.get(index);
            if (!considered.contains(dailyBalance.getIban())) {
                ret = ret.add(dailyBalance.getTotal());
                considered.add(dailyBalance.getIban());
            }
            index--;
        }
        return ret;
    }


    public BigDecimal sumLatestTotalsByIbanFunctionalOriginalCode(List<DailyBalance> dailyBalances) {
        return dailyBalances
            .stream()
            .collect(groupingBy(DailyBalance::getIban))
            .values()
            .stream()
            .flatMap(balances -> balances.stream()
                .max(comparing(DailyBalance::getDay))
                .stream())
            .map(DailyBalance::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal sumLatestTotalsByIbanFunctionalImprovedCode(List<DailyBalance> dailyBalances) {
        return dailyBalances.stream()
            .collect(groupingBy(DailyBalance::getIban, maxBy(DailyBalance.DayComparator)))
            .values().stream()
            .map(balance -> balance.get().getTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
