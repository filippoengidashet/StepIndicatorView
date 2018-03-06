# StepIndicatorView



![Simple step indicator demo](https://raw.githubusercontent.com/filippella/StepIndicatorView/master/demo/simple-demo.png)

Set Indicator Usage

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StepIndicatorView siv = findViewById(R.id.step_indicator_view);
        siv.setStepsCount(3);
        siv.setCurrentStepPosition(1);

        //siv.setAllTicked(); Use this method if you want all to be selected
    }
  }

```

License
-------

    Copyright 2018 Filippo Engidashet.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
