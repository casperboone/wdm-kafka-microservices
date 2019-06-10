import os
import argparse
import csv

import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

parser = argparse.ArgumentParser()
# path of the csv folder with data points
parser.add_argument("--experimentsFolder", "-e", default=os.path.join(".", "experiments"))

if __name__ == "__main__":
    # path of the csv
    args = parser.parse_args()
    data_csv_folder_path = args.experimentsFolder
    type = "unpartitioned"
    data_csv_folder_path = os.path.join(".", "experiments", "output_" + type)

    data = []
    experiment_output_files = os.listdir(data_csv_folder_path)
    for experiment_output_file in experiment_output_files:
        experiment_output_file_parts = experiment_output_file.split('_')
        exp = experiment_output_file_parts[0][:1]
        number_of_users = int(experiment_output_file_parts[0][1:])
        type_experiment = experiment_output_file_parts[1]
        if type_experiment == 'requests.csv':
            experiment_output_path = os.path.join(data_csv_folder_path, experiment_output_file)
            with open(experiment_output_path, 'r') as f:
                reader = csv.DictReader(f)
                for r in reader:
                    fraction_failures = 0
                    if int(r['# requests']) > 0:
                        fraction_failures = int(r['# failures'])/int(r['# requests'])
                    data.append({
                        'Total users': number_of_users,
                        'Method': r['Method'],
                        'Call': r['Name'],
                        '# requests': int(r['# requests']),
                        '# failures': int(r['# failures']),
                        'Fraction of failures': fraction_failures,
                        'Median response time (ms)': int(r['Median response time']),
                        'Average response time (ms)': int(r['Average response time']),
                        'Min response time (ms)': int(r['Min response time']),
                        'Max response time (ms)': int(r['Max response time']),
                        'Average Content Size': int( r['Average Content Size']),
                        'Requests/s': float(r['Requests/s'])
                    })

    category = 'Call'
    df = pd.DataFrame(data).sort_values(by=[category])

    x = 'Total users'
    max_x = 10000

    y_tuples = [
        ('Fraction of failures', 1.0),
        ('Median response time (ms)', 50000),
        ('Average response time (ms)', 50000),
        ('Requests/s', 350),
    ]

    for y_tuple in y_tuples:
        y = y_tuple[0]
        max_y = y_tuple[1]

        title = 'Effect of ' + x + ' on ' + y
        title += ' (' + type + ')'

        sns.set(rc={'figure.figsize': (7.5, 5)})

        g = sns.lineplot(x, y, data=df, hue=category, style=category, markers=True, ci="sd")

        g.legend(loc='upper left')

        # Set base axis at 0,0
        plt.ylim(0, None)
        plt.xlim(0, None)

        plt.title(title)

        plt.show()
